package io.beautifulfruit.finalproject.etcd.connection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NativeConnectionConfigurator {

    @Bean
    public NativeConnectionWrapper nativeConnectionWrapper() {
        var env = System.getenv();
        var endpoints = new java.util.ArrayList<String>();

        int index = 0;
        while (true) {
            String envVarName = "ETCD_ENDPOINT_" + index;
            String endpoint = env.get(envVarName);
            if (endpoint == null) {
                break;
            }
            endpoints.add(endpoint);
            index++;
        }

        return new NativeConnectionWrapper(endpoints);
    }
}