package io.beautifulfruit.finalproject.k8s.connection;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiException;

import java.util.concurrent.CompletableFuture;

public interface ConnectionWrapper {
    public CompletableFuture<Void> applySingleObject(KubernetesObject object) throws ApiException;
}
