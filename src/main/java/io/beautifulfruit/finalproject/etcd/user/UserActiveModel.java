package io.beautifulfruit.finalproject.etcd.user;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import io.beautifulfruit.finalproject.k8s.Quota;

import java.util.ArrayList;

public class UserActiveModel {
    private static final Argon2 argon2 = Argon2Factory.create();

    public String name;
    public String passwordHash;
    public Quota quota;

    DeploymentSuperblock superblock = new DeploymentSuperblock(null);

    public UserActiveModel(UserModel model) {
        this.name = model.username;
        this.passwordHash = model.passwordHash;
        this.quota = new Quota(model.quota.cpu, model.quota.disk, model.quota.memory);
        this.superblock = model.superblock;
    }

    public UserActiveModel(String name, String passwordHash) {
        this.name = name;
        this.passwordHash = argon2.hash(10, 65536, 1, passwordHash.getBytes());
        this.quota = new Quota(2048, 40, 2048);
    }

    public boolean passwordMatch(String password) {
        return argon2.verify(passwordHash, password.getBytes());
    }

    public ArrayList<String> getOwnDeploymentID() {
        return (ArrayList<String>) superblock.uuids.clone();
    }

    public void addDeploymentID(String uuid) {
        // TODO: actually do diff for concurrent access
        superblock.uuids.add(uuid);
    }
}