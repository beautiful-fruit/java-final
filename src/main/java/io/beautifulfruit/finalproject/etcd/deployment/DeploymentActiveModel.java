package io.beautifulfruit.finalproject.etcd.deployment;

// Note: No Argon2 equivalent is typically needed for Deployment,
//       whereas UserActiveModel had password hashing logic.

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

    // Constructor for creating a new deployment before saving
    public DeploymentActiveModel(String uuid, String dockercomposeText, String ownername, int cpu, int memory, int disk) {
        this.uuid = uuid;
        this.dockercomposeText = dockercomposeText;
        this.ownername = ownername;
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
    }

    // Optional: Constructor with default resource values
    public DeploymentActiveModel(String uuid, String dockercomposeText, String ownername) {
        this.uuid = uuid;
        this.dockercomposeText = dockercomposeText;
        this.ownername = ownername;
        // Use default values for cpu, memory, disk defined above
    }
}