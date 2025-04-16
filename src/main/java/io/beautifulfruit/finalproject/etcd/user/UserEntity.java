package io.beautifulfruit.finalproject.etcd.user;

import io.beautifulfruit.finalproject.etcd.connection.NativeConnectionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
public class UserEntity {
    private static final String USER_PREFIX = "user";

    @Autowired
    private final NativeConnectionWrapper connection;

    @Autowired
    public UserEntity(NativeConnectionWrapper connection) {
        this.connection = connection;
    }

    public CompletableFuture<Void> saveUser(UserActiveModel user) {
        byte[] key = user.name.getBytes(StandardCharsets.UTF_8);

        UserModel userModel = new UserModel(user);
        byte[] value = userModel.toJson().getBytes(StandardCharsets.UTF_8);

        return connection.store(USER_PREFIX, key, value);
    }

    public CompletableFuture<UserActiveModel> findUserByName(String name) {
        byte[] key = name.getBytes(StandardCharsets.UTF_8);

        return connection.get(USER_PREFIX, key)
                .thenApply(bytes -> {
                    if (bytes == null) {
                        return null;
                    }

                    UserModel model = new UserModel(bytes);

                    return new UserActiveModel(model);
                });
    }

    public CompletableFuture<Boolean> userExists(String name) {
        byte[] key = name.getBytes(StandardCharsets.UTF_8);

        return connection.get(USER_PREFIX, key)
                .thenApply(Objects::nonNull);
    }
}