package io.beautifulfruit.finalproject.etcd.deployment;

import io.beautifulfruit.finalproject.etcd.connection.NativeConnectionWrapper;
import io.beautifulfruit.finalproject.k8s.resource.NativeK8sResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
public class DeploymentEntity {
    private static final String DEPLOYMENT_PREFIX = "deployment";

    @Autowired
    private final NativeConnectionWrapper connection;

    @Autowired
    public DeploymentEntity(NativeConnectionWrapper connection) {
        this.connection = connection;
    }

    public CompletableFuture<Void> saveDeployment(DeploymentActiveModel deployment) throws Exception {
        byte[] key = deployment.uuid.getBytes(StandardCharsets.UTF_8);

        DeploymentModel deploymentModel = new DeploymentModel(deployment);
        byte[] value = deploymentModel.toJson().getBytes(StandardCharsets.UTF_8);

        NativeK8sResources resources = new NativeK8sResources(deploymentModel.dockercompose_text);

        // WARNING: directly applying the resources to the cluster cause throw an exception
        // 1. resource already exists
        // 2. cluster is not ready
        // TODO: display error to user
        resources.apply();

        return connection.store(DEPLOYMENT_PREFIX, key, value);
    }

    public CompletableFuture<Void> updateDeployment(DeploymentActiveModel deployment) throws Exception {
        // TODO: support update
        return this.saveDeployment(deployment);
    }

    public CompletableFuture<DeploymentActiveModel> findDeploymentByUuid(String uuid) {
        byte[] key = uuid.getBytes(StandardCharsets.UTF_8);

        return connection.get(DEPLOYMENT_PREFIX, key)
                .thenApply(bytes -> {
                    if (bytes == null) {
                        return null;
                    }

                    DeploymentModel model = new DeploymentModel(bytes);

                    return new DeploymentActiveModel(model);
                });
    }

    public CompletableFuture<Boolean> deploymentExists(String uuid) {
        byte[] key = uuid.getBytes(StandardCharsets.UTF_8);

        return connection.get(DEPLOYMENT_PREFIX, key)
                .thenApply(Objects::nonNull);
    }
}