package io.beautifulfruit.finalproject.k8s;

import java.util.concurrent.CompletableFuture;

public class MockK8sResources implements K8sResources {
    @Override
    public CompletableFuture<Void> apply() {
        // TODO: mock the apply method
        return null;
    }
}
