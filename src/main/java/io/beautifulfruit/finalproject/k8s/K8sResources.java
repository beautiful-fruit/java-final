package io.beautifulfruit.finalproject.k8s;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1HTTPIngressPath;
import io.kubernetes.client.openapi.models.V1HTTPIngressRuleValue;
import io.kubernetes.client.openapi.models.V1HostPathVolumeSource;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1IngressBackend;
import io.kubernetes.client.openapi.models.V1IngressRule;
import io.kubernetes.client.openapi.models.V1IngressServiceBackend;
import io.kubernetes.client.openapi.models.V1IngressSpec;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimSpec;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimVolumeSource;
import io.kubernetes.client.openapi.models.V1PersistentVolumeSpec;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBackendPort;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.openapi.models.V1VolumeResourceRequirements;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;

/**
 * K8sResources is a class that represents a list of kubernetes resources
 */
public class K8sResources {
    /**
     * The namespace to use
     */
    private static final String namespace = "default";
    /**
     * The name of the ingress class to use
     */
    private static final String ingressClass = "nginx";
    /**
     * The name of the storage class to use
     */
    private static final String storageClass = "standard";

    static {
        String customConfigPath = System.getenv("KUBECONFIG");
        ApiClient client;

        try {
            if (customConfigPath != null) {
                client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(customConfigPath))).build();
            } else {
                client = Config.defaultClient();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load kubeconfig", e);
        }
        Configuration.setDefaultApiClient(client);
    }

    UUID uuid;
    String serviceName;
    /**
     * A List of kubernetes resources
     * <p>
     * warning: Must be applied in order
     */
    private List<KubernetesObject> objects = new ArrayList<>();

    /**
     * Create a new K8sResources object
     *
     * @param objects The kubernetes objects to use
     */
    public K8sResources(KubernetesObject[] objects) {
        this.objects = List.of(objects);
    }

    public K8sResources(UUID uuid, String dockerCompose) throws Exception {
        this.uuid = uuid;
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(dockerCompose);
        this.serviceName = "autogen-service-" + uuid.toString() + "-name";
        try {
            if (!data.containsKey("services")) throw new Exception("No services found");
            ComposeServices services = new ComposeServices(data.get("services")); // create services via data map
            this.objects.add(parseDeployment(services));

            Integer container80Port = (services.services.stream()
                    .map(s -> s.ports.containerPortMap80()))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (container80Port != null) {
                this.objects.add(parseIngress());
                this.objects.add(parseServices(container80Port));
            }
            this.objects.addAll(parseVolumnes(data.get("services"), uuid));
            this.objects.addAll(parsePersistentVolumeClaim(data.get("services")));
        } catch (Exception e) {
            System.err.println("Error parsing docker compose file: " + e.getMessage());
            throw new Exception("Error parsing docker compose file: " + e.getMessage());
        }
        System.out.println("Parsed " + data.size() + " objects");
    }

    private static List<KubernetesObject> parseVolumnes(Object raw, UUID uuid) {
        Map<String, Object> map = (Map<String, Object>) raw;
        List<KubernetesObject> objects = new ArrayList<>();
        map.forEach((k, v) -> {
            V1PersistentVolume pv = new V1PersistentVolume();
            pv.setApiVersion("v1");
            pv.setKind("PersistentVolume");
            pv.setMetadata(new V1ObjectMeta().name(uuid.toString() + k));
            pv.setSpec(new V1PersistentVolumeSpec()
                    .capacity(Map.of("storage", new Quantity("10Gi")))
                    .accessModes(List.of("ReadWriteOnce"))
                    .persistentVolumeReclaimPolicy("Retain")
                    .storageClassName(storageClass)
                    .hostPath(new V1HostPathVolumeSource().path(v.toString())));
            objects.add(pv);
        });
        return objects;
    }

    private static CompletableFuture<Void> applySingleObject(KubernetesObject object) throws ApiException {
        NetworkingV1Api networking = new NetworkingV1Api();
        CoreV1Api core = new CoreV1Api();

        if (object instanceof V1Ingress) {
            CoreAPICallback<V1Ingress> callback = new CoreAPICallback<>();
            networking.createNamespacedIngress(namespace, (V1Ingress) object).executeAsync(callback);
            return callback.future;
        }

        if (object instanceof V1Pod) {
            CoreAPICallback<V1Pod> callback = new CoreAPICallback<>();
            core.createNamespacedPod(namespace, (V1Pod) object).executeAsync(callback);
            return callback.future;
        }
        if (object instanceof V1PersistentVolume) {
            CoreAPICallback<V1PersistentVolume> callback = new CoreAPICallback<>();
            core.createPersistentVolume((V1PersistentVolume) object).executeAsync(callback);
            return callback.future;
        }
        if (object instanceof V1PersistentVolumeClaim) {
            CoreAPICallback<V1PersistentVolumeClaim> callback = new CoreAPICallback<>();
            core.createNamespacedPersistentVolumeClaim(namespace, (V1PersistentVolumeClaim) object).executeAsync(callback);
            return callback.future;
        }

        if (object instanceof V1Deployment) {
            AppsV1Api apps = new AppsV1Api();
            CoreAPICallback<V1Deployment> callback = new CoreAPICallback<>();
            apps.createNamespacedDeployment(namespace, (V1Deployment) object).executeAsync(callback);
            return callback.future;
        }
        if (object instanceof V1Service) {
            CoreAPICallback<V1Service> callback = new CoreAPICallback<>();
            core.createNamespacedService(namespace, (V1Service) object).executeAsync(callback);
            return callback.future;
        } else {
            throw new RuntimeException("Pods not supported yet");
        }
    }

    /**
     * Delete the objects in the list, with dependency in mind
     *
     * @param objects The objects to delete
     * @return A CompletableFuture that will be completed when the objects are deleted
     */
    private static CompletableFuture<Void> maybeDeleteObjects(List<KubernetesObject> objects) {
        // TODO: implement this
        return CompletableFuture.completedFuture(null);
    }

    public List<V1PersistentVolumeClaim> parsePersistentVolumeClaim(Object raw) {
        ComposeServices services = new ComposeServices(raw);

        List<V1PersistentVolumeClaim> objects = new ArrayList<>();

        List<String> mounts = new ArrayList<>();
        for (ComposeServices.Service service : services.services) {
            for (ComposeMounts.Mount mount : service.mounts.mounts) {
                mounts.add(mount.name);
            }
        }
        mounts = mounts.stream().distinct().collect(Collectors.toList());
        for (String mount : mounts) {
            V1PersistentVolumeClaim pvc = new V1PersistentVolumeClaim();
            pvc.setApiVersion("v1");
            pvc.setKind("PersistentVolumeClaim");
            pvc.setMetadata(new V1ObjectMeta().name(uuid.toString() + mount));
            pvc.setSpec(new V1PersistentVolumeClaimSpec()
                    .accessModes(List.of("ReadWriteOnce"))
                    .resources(new V1VolumeResourceRequirements()
                            .requests(Map.of("storage", new Quantity("10Gi"))))
                    .storageClassName(storageClass));
            objects.add(pvc);
        }

        return objects;
    }

    public V1Deployment parseDeployment(ComposeServices services) {
        V1PodSpec spec = new V1PodSpec();

        for (ComposeServices.Service service : services.services) {
            V1Container container = new V1Container();
            container.setName(service.name);
            container.setImage(service.image);
            if (service.ports.hasPort80()) {
                container = container.addPortsItem(new V1ContainerPort().containerPort(service.ports.containerPortMap80()));
            }
            for (ComposeMounts.Mount mount : service.mounts.mounts) {
                container = container.addVolumeMountsItem(new V1VolumeMount()
                        .mountPath(mount.mountPath)
                        .name(mount.name));
                spec = spec.addVolumesItem(new V1Volume()
                        .name(mount.name)
                        .persistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource()
                                .claimName(uuid.toString() + mount.name)));
            }
            spec = spec.addContainersItem(container);
        }
        V1Deployment deployment = new V1Deployment();
        deployment.setApiVersion("apps/v1");
        deployment.setKind("Deployment");
        deployment.setMetadata(new V1ObjectMeta().name(uuid.toString()));
        deployment.setSpec(new V1DeploymentSpec()
                .replicas(1)
                .selector(new V1LabelSelector().matchLabels(Map.of("app", uuid.toString())))
                .template(new V1PodTemplateSpec()
                        .metadata(new V1ObjectMeta().labels(Map.of("app", uuid.toString())))
                        .spec(spec)));

        return deployment;
    }

    public V1Ingress parseIngress() {
        V1Ingress ingress = new V1Ingress();
        ingress.setApiVersion("networking.k8s.io/v1");
        ingress.setKind("Ingress");
        ingress.setMetadata(new V1ObjectMeta().name(uuid.toString()));
        ingress.setSpec(new V1IngressSpec().ingressClassName(ingressClass)
                .addRulesItem(new V1IngressRule()
                        .host("example.com")
                        .http(new V1HTTPIngressRuleValue()
                                .addPathsItem(new V1HTTPIngressPath()
                                        .path("/" + uuid.toString())
                                        .pathType("Prefix")
                                        .backend(new V1IngressBackend()
                                                .service(new V1IngressServiceBackend()
                                                        .name(serviceName)
                                                        .port(new V1ServiceBackendPort().number(80))))))));
        return ingress;
    }

    public V1Service parseServices(int targetPort) {
        V1Service service = new V1Service();
        service.setApiVersion("v1");
        service.setKind("Service");
        service.setMetadata(new V1ObjectMeta().name(serviceName));
        service.setSpec(new V1ServiceSpec()
                .type("ClusterIP")
                .selector(Map.of("app", uuid.toString()))
                .ports(List.of(new V1ServicePort()
                        .port(80)
                        .targetPort(new IntOrString(targetPort)))));
        return service;
    }

    public CompletableFuture<Void> apply() {

        CompletableFuture<Void> current = CompletableFuture.completedFuture(null);

        for (KubernetesObject object : objects) {
            current = current.thenCompose(v -> {
                try {
                    return K8sResources.applySingleObject(object);
                } catch (ApiException e) {
                    // TODO: use logger
                    System.err.println("Error applying object: " + e.getMessage());
                    System.err.println("skipping object: " + object);
                }
                return null;
            });
        }

        return current;
    }

    /**
     * Delete the objects in the cluster
     *
     * @return A CompletableFuture that will be completed when the objects are deleted
     */
    public CompletableFuture<Void> delete() {
        return maybeDeleteObjects(objects);
    }

    static class CoreAPICallback<T extends KubernetesObject> implements ApiCallback<T> {
        CompletableFuture<Void> future = new CompletableFuture<>();

        @Override
        public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
            future.completeExceptionally(e);
        }

        @Override
        public void onSuccess(T result, int statusCode, Map<String, List<String>> responseHeaders) {
            future.complete(null);

        }

        @Override
        public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

        }

        @Override
        public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

        }
    }

    static class ComposeMounts {
        public List<Mount> mounts = new ArrayList<>();

        public void addMount(Mount mount) {
            this.mounts.add(mount);
        }

        static class Mount {
            public String mountPath;
            public String name;

            public Mount(Object raw) {
                if (raw instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) raw;
                    this.mountPath = (String) map.get("mountPath");
                    this.name = (String) map.get("name");
                } else if (raw instanceof String) {
                    String[] parts = ((String) raw).split(":");
                    if (parts.length == 2) {
                        this.name = parts[0];
                        this.mountPath = parts[1];
                    } else {
                        throw new RuntimeException("Invalid mount format");
                    }
                } else {
                    throw new RuntimeException("Invalid mount format");
                }
            }
        }
    }


    static class ComposeServices {
        public List<Service> services = new ArrayList<>();

        public ComposeServices(Object raw) {
            Map<String, Object> map = (Map<String, Object>) raw;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String name = entry.getKey();
                Service service = new Service(name, entry.getValue());
                services.add(service);
            }
        }

        private static class Service {
            public String name;
            public String image;
            public ComposePorts ports = new ComposePorts();
            public ComposeMounts mounts = new ComposeMounts();

            public Service(String name, Object raw) {
                Map<String, Object> map = (Map<String, Object>) raw;
                this.name = name;
                this.image = (String) map.get("image");
                if (map.containsKey("ports")) {
                    this.ports = new ComposePorts(map.get("ports"));
                }
                if (map.containsKey("volumes")) {
                    List<Object> volumes = (List<Object>) map.get("volumes");
                    for (Object volume : volumes) {
                        mounts.addMount(new ComposeMounts.Mount(volume));
                    }
                }
            }
        }
    }

    static class ComposePorts {
        ArrayList<Port> ports;

        public ComposePorts() {
            this.ports = new ArrayList<>();
        }

        public ComposePorts(Object raw) {
            List<Object> ports = (List<Object>) raw;
            this.ports = ports.stream()
                    .map(Port::new)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public boolean hasPort80() {
            for (Port port : ports) {
                if (port.port == 80) {
                    return true;
                }
            }
            return false;
        }

        public Integer containerPortMap80() {
            for (Port port : ports) {
                if (port.port == 80) {
                    return port.targetPort;
                }
            }
            return null;
        }

        private static class Port {
            private final int port;
            private final int targetPort;
            private String protocol;

            public Port(Object raw) {
                if (raw instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) raw;
                    this.protocol = (String) map.get("protocol");
                    this.port = (int) map.get("port");
                    this.targetPort = Integer.parseInt((String) map.get("targetPort"));
                } else if (raw instanceof String) {
                    this.protocol = "TCP";
                    String[] parts = ((String) raw).split("[:/]");
                    this.port = Integer.parseInt(parts[0]);
                    this.targetPort = Integer.parseInt(parts[1]);
                    if (parts.length == 3) {
                        this.protocol = parts[2];
                    }
                } else {
                    throw new RuntimeException("Invalid port format");
                }
            }
        }
    }
}
