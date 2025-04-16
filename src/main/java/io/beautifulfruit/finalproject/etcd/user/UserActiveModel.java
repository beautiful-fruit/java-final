package io.beautifulfruit.finalproject.etcd.user;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class UserActiveModel {
    private static final Argon2 argon2 = Argon2Factory.create();

    public String name;
    public String passwordHash;
    public int cpu;
    public int memory;
    public int disk;

    public UserActiveModel(UserModel model) {
        this.name = model.username;
        this.passwordHash = model.passwordHash;
        this.cpu = model.cpu;
        this.memory = model.memory;
        this.disk = model.disk;
    }

    public UserActiveModel(String name, String passwordHash) {
        this.name = name;
        this.passwordHash = argon2.hash(10, 65536, 1, passwordHash.getBytes());
    }

    public boolean passwordMatch(String password) {
        return argon2.verify(passwordHash, password.getBytes());
    }
}