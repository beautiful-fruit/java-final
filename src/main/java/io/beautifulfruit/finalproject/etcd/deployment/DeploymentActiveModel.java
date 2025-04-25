package io.beautifulfruit.finalproject.etcd.deployment;

public class DeploymentActiveModel {
    public String uuid; // deployment_uuid
    public String dockercomposeText; // dockercompose_text
    public String ownername; // ownername
    public int cpu = 40; // Default values, e.g., 40 for 0.4 CPU share
    public int memory = 2048; // MB
    public int disk = 2048; // MB

    public DeploymentActiveModel(DeploymentModel model) {
        this.uuid = model.deployment_uuid;
        this.dockercomposeText = model.dockercompose_text;
        this.ownername = model.ownername;
        this.cpu = model.cpu;
        this.memory = model.memory;
        this.disk = model.disk;
    }

    public DeploymentActiveModel(String uuid, String dockercomposeText, String ownername, int cpu, int memory, int disk) {
        this.uuid = uuid;
        this.dockercomposeText = dockercomposeText;
        this.ownername = ownername;
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
    }

    public DeploymentActiveModel(String uuid, String dockercomposeText, String ownername) {
        this.uuid = uuid;
        this.dockercomposeText = dockercomposeText;
        this.ownername = ownername;
    }
}