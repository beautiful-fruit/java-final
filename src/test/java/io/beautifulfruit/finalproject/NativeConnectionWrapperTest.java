package io.beautifulfruit.finalproject;

import io.beautifulfruit.finalproject.etcd.NativeConnectionWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
public class NativeConnectionWrapperTest {

    @Autowired
    private NativeConnectionWrapper wrapper;

    @Test
    void testStoreAndGet() {
        assumeTrue(System.getenv("ETCD_ENDPOINT_0") != null, "ETCD_ENDPOINT_0 not set");

        byte[] key = "springTestKey".getBytes();
        byte[] value = "springTestValue".getBytes();

        wrapper.store("prefix", key, value).join();

        byte[] result = wrapper.get("prefix", "springTestKey".getBytes()).join();

        assertNotNull(result);
        assertArrayEquals(value, result);
    }
}