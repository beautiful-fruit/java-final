package io.beautifulfruit.finalproject.etcd.deployment;

import io.beautifulfruit.finalproject.etcd.connection.NativeConnectionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeploymentConfigurator {
    private final NativeConnectionWrapper wrapper;

    @Autowired
    public DeploymentConfigurator(NativeConnectionWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Bean
    public DeploymentEntity deploymentEntity() {
        return new DeploymentEntity(wrapper);
    }
}