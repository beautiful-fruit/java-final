package io.beautifulfruit.finalproject.etcd.deployment;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class DeploymentModel {
    private static final Gson gson = new Gson();

    public final String deployment_uuid;
    public final String dockercompose_text;
    public final String ownername;
    public int cpu; // e.g., 0.1 CPU represented as some integer
    public int memory; // MB
    public int disk; // MB

    public DeploymentModel(byte[] json) {
        DeploymentModel model = gson.fromJson(new String(json, StandardCharsets.UTF_8), DeploymentModel.class);

        this.deployment_uuid = model.deployment_uuid;
        this.dockercompose_text = model.dockercompose_text;
        this.ownername = model.ownername;
        this.cpu = model.cpu;
        this.memory = model.memory;
        this.disk = model.disk;
    }

    public DeploymentModel(DeploymentActiveModel deployment) {
        this.deployment_uuid = deployment.uuid;
        this.dockercompose_text = deployment.dockercomposeText;
        this.ownername = deployment.ownername;
        this.cpu = deployment.cpu;
        this.memory = deployment.memory;
        this.disk = deployment.disk;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}