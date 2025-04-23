package io.beautifulfruit.finalproject.etcd.deployment;

import io.beautifulfruit.finalproject.etcd.connection.NativeConnectionWrapper;
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

    public CompletableFuture<Void> saveDeployment(DeploymentActiveModel deployment) {
        byte[] key = deployment.uuid.getBytes(StandardCharsets.UTF_8);

        DeploymentModel deploymentModel = new DeploymentModel(deployment);
        byte[] value = deploymentModel.toJson().getBytes(StandardCharsets.UTF_8);

        // TODO: when save, check if the deployment already exist, if not, call k8s to create a new deployment(deployment is not a k8s concept here)

        return connection.store(DEPLOYMENT_PREFIX, key, value);
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