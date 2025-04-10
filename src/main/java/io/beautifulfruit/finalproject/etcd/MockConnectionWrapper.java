package io.beautifulfruit.finalproject.etcd;

import java.util.concurrent.CompletableFuture;

public class MockConnectionWrapper implements ConnectionWrapper {
    public CompletableFuture<Void> store(Byte[] key, Byte[] value) {
        // TODO: mock the store method
        return CompletableFuture.runAsync(() -> {
        });
    }

    public CompletableFuture<Byte[]> get(String key) {
        // TODO: mock the get method
        throw new RuntimeException("Not implemented yet");
    }
}
