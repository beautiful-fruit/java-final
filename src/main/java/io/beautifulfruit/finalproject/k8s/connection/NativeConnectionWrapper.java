package io.beautifulfruit.finalproject.k8s.connection;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class NativeConnectionWrapper implements ConnectionWrapper {

    private final static String NAMESPACE = "default";
    private final CoreV1Api api;

    public NativeConnectionWrapper() throws IOException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        api = new CoreV1Api();
    }

    @Override
    public CompletableFuture<Void> applySingleObject(KubernetesObject object) throws ApiException {
        if (object instanceof V1Pod pod) {
            api.createNamespacedPod(NAMESPACE, pod, null, null, null, null);
        } else {
            throw new RuntimeException("Unsupported object type: " + object.getClass().getName());
        }
        throw new RuntimeException("Pods not supported yet");
    }
}
