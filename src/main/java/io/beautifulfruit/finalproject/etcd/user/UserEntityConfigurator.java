package io.beautifulfruit.finalproject.etcd.user;

import io.beautifulfruit.finalproject.etcd.connection.NativeConnectionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserEntityConfigurator {
    @Autowired
    private NativeConnectionWrapper wrapper;

    @Bean
    public UserEntity userEntity() {
        return new UserEntity(wrapper);
    }
}