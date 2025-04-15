package io.beautifulfruit.finalproject.etcd;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MockConnectionWrapper implements ConnectionWrapper {
    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    /**
     * Create a new MockConnectionWrapper object
     */
    public MockConnectionWrapper() {
    }

    /**
     * Create a new MockConnectionWrapper object
     *
     * @param prefix  the prefix to use as a key namespace
     * @param storage the storage to use
     */
    public MockConnectionWrapper(String prefix, Map<String, byte[]> storage) {
        if (storage != null) {
            // Copy entries with the prefix prepended
            for (Map.Entry<String, byte[]> entry : storage.entrySet()) {
                this.storage.put(prefix + entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public CompletableFuture<Void> store(String prefix, byte[] key, byte[] value) {
        String fullKey = prefix + new String(key, StandardCharsets.UTF_8);
        storage.put(fullKey, value);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<byte[]> get(String prefix, byte[] key) {
        String fullKey = prefix + new String(key, StandardCharsets.UTF_8);
        byte[] value = storage.get(fullKey);
        return CompletableFuture.completedFuture(value);
    }
}