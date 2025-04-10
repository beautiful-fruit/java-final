package io.beautifulfruit.finalproject.k8s;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * K8sResources is a class that represents a list of kubernetes resources
 */
public class NativeK8sResources implements K8sResources {
    static {
        // TODO: load kubernetes config
        // ApiClient client = Config.defaultClient();
        // Configuration.setDefaultApiClient(client);
        // the code above load the kubernetes config from the default location
    }

    /**
     * The namespace to use
     */
    private final String namespace = "default";
    /**
     * The name of the ingress class to use
     */
    private final String ingressClass = "nginx";
    /**
     * The name of the storage class to use
     */
    private final String storageClass = "standard";
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
    public NativeK8sResources(String dockerCompose) {
        // TODO: generate a random name for prefix
        // TODO: find volumes in docker compose file, parse them to k8s persistent volume
        this.objects.add(new V1PersistentVolumeClaim());
        // TODO: parse labels and create services and ingress
        this.objects.add(new V1Service());
        this.objects.add(new V1Ingress());
        // TODO: parse docker compose file to k8s deployment
        this.objects.add(new V1Deployment());
    }

    private static CompletableFuture<Void> applySingleObject(CoreV1Api api, KubernetesObject object) {
        if (object instanceof V1Pod) {
            // TODO: apply pod
            throw new RuntimeException("Not implemented yet");
            // TODO: handle other types of objects
        } else {
            throw new RuntimeException("Pods not supported yet");
        }
    }

    public CompletableFuture<Void> apply() {
        CoreV1Api api = new CoreV1Api();

        CompletableFuture<Void> current = CompletableFuture.completedFuture(null);

        for (KubernetesObject object : objects) {
            current = current.thenCompose(v -> NativeK8sResources.applySingleObject(api, object));
        }

        return current;
    }
}
