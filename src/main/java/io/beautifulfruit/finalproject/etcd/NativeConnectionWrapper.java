package io.beautifulfruit.finalproject.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

/**
 * NativeConnectionWrapper is a wrapper around the etcd client
 */
@Component
public class NativeConnectionWrapper {
    /**
     * Delimiter used to separate the prefix and the key
     * <p>
     * Must not be used in the prefix or the key
     */
    public static final Byte delimiter = (byte) 0xDF;
    private Client client;

    /**
     * Create a new NativeConnectionWrapper object
     *
     * @param endpoints the list of endpoints to connect to
     */
    public NativeConnectionWrapper(List<String> endpoints) {
        if (!endpoints.isEmpty()) {
            Iterable<URI> uris = endpoints.stream().map(URI::create).collect(Collectors.toList());
            client = Client.builder().endpoints(uris).build();
        }
        // TODO: log warning if endpoints is empty
    }

    private ByteSequence concatKey(String prefix, byte[] key) {
        byte[] prefixBytes = prefix.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(prefixBytes.length + 1 + key.length);
        buffer.put(prefixBytes);
        buffer.put(delimiter);
        buffer.put(key);
        return ByteSequence.from(buffer.array());
    }


    public CompletableFuture<Void> store(String prefix, byte[] key, byte[] value) {
        KV kvClient = client.getKVClient();
        ByteSequence fullKey = concatKey(prefix, key);
        ByteSequence val = ByteSequence.from(value);

        return kvClient.put(fullKey, val)
                .thenAccept((PutResponse resp) -> {
                });
    }

    public CompletableFuture<byte[]> get(String prefix, byte[] key) {
        KV kvClient = client.getKVClient();
        ByteSequence fullKey = concatKey(prefix, key);

        return kvClient.get(fullKey)
                .thenApply((GetResponse resp) -> {
                    if (resp.getKvs().isEmpty()) {
                        return null;
                    } else {
                        return resp.getKvs().getFirst().getValue().getBytes();
                    }
                })
                .exceptionally(ex -> {
                    throw new CompletionException(ex);
                });
    }
}
