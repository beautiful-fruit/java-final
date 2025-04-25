package io.beautifulfruit.finalproject.etcd.user;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.util.ArrayList;
import java.util.UUID;

public class UserActiveModel {
    private static final Argon2 argon2 = Argon2Factory.create();

    public String name;
    public String passwordHash;
    public int cpu = 40;
    public int memory = 2048;
    public int disk = 2048;

    DeploymentSuperblock superblock = new DeploymentSuperblock(null);

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

    public ArrayList<UUID> getOwnDeploymentID() {
        return (ArrayList<UUID>) superblock.uuids.clone();
    }

    public void addDeploymentID(UUID uuid) {
        // TODO: actually do diff for concurrent access
        superblock.uuids.add(uuid);
    }
}