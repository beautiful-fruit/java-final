package io.beautifulfruit.finalproject.etcd.deployment;

import com.google.gson.Gson;
import io.beautifulfruit.finalproject.k8s.Quota;

import java.nio.charset.StandardCharsets;

public class DeploymentModel {
    private static final Gson gson = new Gson();

    public final String deployment_uuid;
    public final String dockercompose_text;
    public final String ownername;
    public final Quota quota;
    public String displayStatus;

    public DeploymentModel(byte[] json) {
        DeploymentModel model = gson.fromJson(new String(json, StandardCharsets.UTF_8), DeploymentModel.class);

        this.deployment_uuid = model.deployment_uuid;
        this.dockercompose_text = model.dockercompose_text;
        this.ownername = model.ownername;
        this.quota = model.quota;
        this.displayStatus = model.displayStatus;
    }

    public DeploymentModel(DeploymentActiveModel deployment) {
        this.deployment_uuid = deployment.uuid;
        this.dockercompose_text = deployment.dockercomposeText;
        this.ownername = deployment.ownername;
        this.quota = deployment.quota;
        this.displayStatus = deployment.displayStatus;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}