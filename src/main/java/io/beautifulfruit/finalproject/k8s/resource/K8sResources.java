package io.beautifulfruit.finalproject.k8s.resource;

import java.util.concurrent.CompletableFuture;

public interface K8sResources {
    /**
     * Apply the resources in order
     *
     * @return CompletableFuture<Void>
     */
    CompletableFuture<Void> apply();
}
