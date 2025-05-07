package io.beautifulfruit.finalproject.k8s.resource;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.*;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * K8sResources is a class that represents a list of kubernetes resources
 */
public class NativeK8sResources implements K8sResources {
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
        // TODO: load kubernetes config
        // ApiClient client = Config.defaultClient();
        // Configuration.setDefaultApiClient(client);
        // the code above load the kubernetes config from the default location
    }

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
    public NativeK8sResources(KubernetesObject[] objects) {
        this.objects = List.of(objects);
    }

    /**
     * Create a new K8sResources object
     *
     * @param dockerCompose content of docker compose file
     */
    public NativeK8sResources(String dockerCompose) throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(dockerCompose);
        UUID uuid = UUID.randomUUID();
        // parse volumes
        try {
            if (data.containsKey("volumes")) {
                Map<String, Object> volumes = (Map<String, Object>) data.get("volumes");
                this.objects.addAll(NativeK8sResources.parseVolumnes(volumes, uuid));
            }
            // parse services
            if (data.containsKey("services")) {
                Map<String, Object> services = (Map<String, Object>) data.get("services");
                this.objects.addAll(NativeK8sResources.parseDeployments(services, uuid));
                this.objects.addAll(NativeK8sResources.parseServiceIngress(services, uuid));
            }
        } catch (Exception e) {
            System.err.println("Error parsing docker compose file: " + e.getMessage());
            throw new Exception("Error parsing docker compose file: " + e.getMessage());
        }
    }

    private static List<KubernetesObject> parseVolumnes(Map<String, Object> map, UUID uuid) {
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

    private static List<KubernetesObject> parseDeployments(Map<String, Object> map, UUID uuid) {
        List<KubernetesObject> objects = new ArrayList<>();
        map.forEach((k, v) -> {
            V1Deployment deployment = new V1Deployment();
            deployment.setApiVersion("apps/v1");
            deployment.setKind("Deployment");
            deployment.setMetadata(new V1ObjectMeta().name(uuid.toString() + k));
            deployment.setSpec(new V1DeploymentSpec()
                    .replicas(1)
                    .selector(new V1LabelSelector().matchLabels(Map.of("app", k)))
                    .template(new V1PodTemplateSpec()
                            .metadata(new V1ObjectMeta().labels(Map.of("app", k)))
                            .spec(new V1PodSpec()
                                    .containers(List.of(new V1Container()
                                            .name(k)
                                            .image(v.toString())
                                            .ports(List.of(new V1ContainerPort().containerPort(80))))))));
            objects.add(deployment);
        });
        return objects;
    }

    private static List<KubernetesObject> parseServiceIngress(Map<String, Object> map, UUID uuid) {
        List<KubernetesObject> objects = new ArrayList<>();
        map.forEach((k, v) -> {
            V1Service service = new V1Service();
            service.setApiVersion("v1");
            service.setKind("Service");
            service.setMetadata(new V1ObjectMeta().name(uuid.toString() + k));
            service.setSpec(new V1ServiceSpec()
                    .type("NodePort")
                    .selector(Map.of("app", k))
                    .ports(List.of(new V1ServicePort()
                            .port(80)
                            .targetPort(new IntOrString(80))
                            .nodePort(30000))));
            objects.add(service);
        });
        if (!objects.isEmpty()) {
            V1Ingress ingress = new V1Ingress();
            ingress.setApiVersion("networking.k8s.io/v1");
            ingress.setKind("Ingress");
            ingress.setMetadata(new V1ObjectMeta().name(uuid.toString() + "ingress"));
            ingress.setSpec(new V1IngressSpec()
                    .ingressClassName(ingressClass)
                    .rules(List.of(new V1IngressRule()
                            .host("localhost")
                            .http(new V1HTTPIngressRuleValue()
                                    .paths(List.of(new V1HTTPIngressPath()
                                            .path("/")
                                            .pathType("Prefix")
                                            .backend(new V1IngressBackend()
                                                    .service(new V1IngressServiceBackend()
                                                            .name(uuid.toString() + "service")
                                                            .port(new V1ServiceBackendPort().number(80))))))))));
            objects.add(ingress);
        }
        return objects;
    }

    private static CompletableFuture<Void> applySingleObject(KubernetesObject object) throws ApiException {
        CoreV1Api core = new CoreV1Api();
        NetworkingV1Api networking = new NetworkingV1Api();

        if (object instanceof V1Pod) {
            CoreAPICallback<V1Pod> callback = new CoreAPICallback<>();
            core.createNamespacedPodAsync(namespace, (V1Pod) object, null, null, null, null, callback);
            return callback.future;
        }
        if (object instanceof V1PersistentVolume) {
            CoreAPICallback<V1PersistentVolume> callback = new CoreAPICallback<>();
            core.createPersistentVolumeAsync((V1PersistentVolume) object, null, null, null, null, callback);
            return callback.future;
        }
        if (object instanceof V1PersistentVolumeClaim) {
            CoreAPICallback<V1PersistentVolumeClaim> callback = new CoreAPICallback<>();
            core.createNamespacedPersistentVolumeClaimAsync(namespace, (V1PersistentVolumeClaim) object, null, null, null, null, callback);
            return callback.future;
        }
        if (object instanceof V1Ingress) {
            CoreAPICallback<V1Ingress> callback = new CoreAPICallback<>();
            networking.createNamespacedIngressAsync(namespace, (V1Ingress) object, null, null, null, null, callback);
            return callback.future;
        }
        if (object instanceof V1Deployment) {
            AppsV1Api apps = new AppsV1Api();
            CoreAPICallback<V1Deployment> callback = new CoreAPICallback<>();
            apps.createNamespacedDeploymentAsync(namespace, (V1Deployment) object, null, null, null, null, callback);
            return callback.future;
        }
        if (object instanceof V1Service) {
            CoreAPICallback<V1Service> callback = new CoreAPICallback<>();
            core.createNamespacedServiceAsync(namespace, (V1Service) object, null, null, null, null, callback);
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

    public CompletableFuture<Void> apply() {

        CompletableFuture<Void> current = CompletableFuture.completedFuture(null);

        for (KubernetesObject object : objects) {
            current = current.thenCompose(v -> {
                try {
                    return NativeK8sResources.applySingleObject(object);
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
}
