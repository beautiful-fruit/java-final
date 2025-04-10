package io.beautifulfruit.finalproject.etcd;

import java.util.concurrent.CompletableFuture;

public interface ConnectionWrapper {
    /**
     * Store the key-value pair in etcd
     *
     * @param key   the key to store
     * @param value the value to store
     * @return CompletableFuture<Void>
     */
    CompletableFuture<Void> store(Byte[] key, Byte[] value);

    /**
     * Get the value for the key from etcd
     *
     * @param key the key to get
     * @return CompletableFuture<Byte [ ]>
     */
    CompletableFuture<Byte[]> get(String key);
}
