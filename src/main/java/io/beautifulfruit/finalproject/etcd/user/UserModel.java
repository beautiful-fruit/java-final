package io.beautifulfruit.finalproject.etcd.user;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class UserModel {
    private static final Gson gson = new Gson();

    public final String username;
    public final String passwordHash;
    public int cpu = 40; // 0.1 CPU
    public int memory = 2 * 1024; // MB
    public int disk = 2 * 1024; // MB

    public UserModel(byte[] json) {
        UserModel model = gson.fromJson(new String(json, StandardCharsets.UTF_8), UserModel.class);

        this.username = model.username;
        this.passwordHash = model.passwordHash;
        this.cpu = model.cpu;
        this.memory = model.memory;
        this.disk = model.disk;
    }

    public UserModel(UserActiveModel user) {
        this.username = user.name;
        this.passwordHash = user.passwordHash;
        this.cpu = user.cpu;
        this.memory = user.memory;
        this.disk = user.disk;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}