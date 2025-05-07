package io.beautifulfruit.finalproject.etcd.user;

import com.google.gson.Gson;
import io.beautifulfruit.finalproject.k8s.Quota;

import java.nio.charset.StandardCharsets;

public class UserModel {
    private static final Gson gson = new Gson();

    public final String username;
    public final String passwordHash;
    public Quota quota;
    DeploymentSuperblock superblock;

    public UserModel(byte[] json) {
        UserModel model = gson.fromJson(new String(json, StandardCharsets.UTF_8), UserModel.class);

        this.username = model.username;
        this.passwordHash = model.passwordHash;
        this.quota = new Quota(model.quota.cpu, model.quota.disk, model.quota.memory);
        this.superblock = model.superblock;
    }

    public UserModel(UserActiveModel user) {
        this.username = user.name;
        this.passwordHash = user.passwordHash;
        this.quota = new Quota(user.quota.cpu, user.quota.disk, user.quota.memory);
        this.superblock = user.superblock;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}