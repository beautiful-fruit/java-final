package io.beautifulfruit.finalproject.etcd.user;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class UserModel {
    private static final Gson gson = new Gson();

    public final String username;
    public final String passwordHash;
    public int cpu; // 0.1 CPU
    public int memory; // MB
    public int disk; // MB

    DeploymentSuperblock superblock;

    public UserModel(byte[] json) {
        UserModel model = gson.fromJson(new String(json, StandardCharsets.UTF_8), UserModel.class);

        this.username = model.username;
        this.passwordHash = model.passwordHash;
        this.cpu = model.cpu;
        this.memory = model.memory;
        this.disk = model.disk;
        this.superblock = model.superblock;
    }

    public UserModel(UserActiveModel user) {
        this.username = user.name;
        this.passwordHash = user.passwordHash;
        this.cpu = user.cpu;
        this.memory = user.memory;
        this.disk = user.disk;
        this.superblock = user.superblock;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}