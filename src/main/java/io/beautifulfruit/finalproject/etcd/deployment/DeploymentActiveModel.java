package io.beautifulfruit.finalproject.etcd.deployment;

import io.beautifulfruit.finalproject.k8s.Quota;

import java.util.UUID;

public class DeploymentActiveModel {
    public String uuid; // deployment_uuid
    public String dockercomposeText; // dockercompose_text
    public String ownername; // ownername
    public Quota quota;
    public String displayStatus = "Pending"; // Default status

    public DeploymentActiveModel(DeploymentModel model) {
        this.uuid = model.deployment_uuid;
        this.dockercomposeText = model.dockercompose_text;
        this.ownername = model.ownername;
        this.quota = model.quota;
    }

    public DeploymentActiveModel(String dockercomposeText, String ownername, Quota quota) {
        this.uuid = UUID.randomUUID().toString();
        this.dockercomposeText = dockercomposeText;
        this.ownername = ownername;
        this.quota = quota;
    }

    public DeploymentActiveModel(String dockercomposeText, String ownername) {
        this.uuid = UUID.randomUUID().toString();
        this.dockercomposeText = dockercomposeText;
        this.ownername = ownername;
        this.quota = Quota.defaultDeploymentQuota();
    }
}