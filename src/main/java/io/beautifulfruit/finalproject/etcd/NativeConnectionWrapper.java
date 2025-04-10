package io.beautifulfruit.finalproject.etcd;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;

import java.util.concurrent.CompletableFuture;

/**
 * NativeConnectionWrapper is a wrapper around the etcd client
 */
public class NativeConnectionWrapper {
    /**
     * Delimiter used to separate the prefix and the key
     * <p>
     * Must not be used in the prefix or the key
     */
    public static final Byte delimiter = (byte) 0xDF;
    private static Client client;

    static {
        // TODO: read env var for endpoints, for example, ETCD_ENDPOINTS_0/ETCD_ENDPOINTS_1
        NativeConnectionWrapper.client = Client.builder().endpoints("http://localhost:2379").build();
    }

    String prefix;

    /**
     * Create a new NativeConnectionWrapper object
     *
     * @param prefix the prefix to use
     */
    public NativeConnectionWrapper(String prefix) {
        this.prefix = prefix;
    }

    public CompletableFuture<Void> store(Byte[] key, Byte[] value) {
        KV kvClient = client.getKVClient();
        // TODO: concatenate prefix and key
        // TODO: put the key-value pair in etcd
        return CompletableFuture.runAsync(() -> {
        });
    }

    public CompletableFuture<Byte[]> get(String key) {
        KV kvClient = client.getKVClient();
        // TODO: get the value for the key from etcd
        throw new RuntimeException("Not implemented yet");
    }
}
