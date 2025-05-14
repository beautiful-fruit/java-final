package io.beautifulfruit.finalproject.etcd.deployment;

import io.beautifulfruit.finalproject.etcd.connection.NativeConnectionWrapper;
import io.beautifulfruit.finalproject.etcd.user.UserEntity;
import io.beautifulfruit.finalproject.k8s.K8sResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class DeploymentEntity {
    private static final String DEPLOYMENT_PREFIX = "deployment";

    @Autowired
    private final NativeConnectionWrapper connection;

    @Autowired
    private UserEntity userEntity;

    @Autowired
    public DeploymentEntity(NativeConnectionWrapper connection) {
        this.connection = connection;
    }

    public CompletableFuture<Void> deleteDeployment(DeploymentActiveModel deployment) throws Exception {
        // TODO: delete the deployment
        return null;
    }

    public CompletableFuture<Void> saveDeployment(DeploymentActiveModel deployment) throws Exception {
        byte[] key = deployment.uuid.toString().getBytes(StandardCharsets.UTF_8);

        DeploymentModel deploymentModel = new DeploymentModel(deployment);

        K8sResources resources = new K8sResources(deployment.uuid, deploymentModel.dockercompose_text);

        return userEntity.findUserByName(deploymentModel.ownername).thenCompose(user -> {
//            if (!user.quota.hasMoreThan(deploymentModel.quota)) {
//                deploymentModel.displayStatus = "Quota exceeded";
//            } else {
            try {
                resources.apply();
            } catch (Exception e) {
                System.out.println("Error applying resources: " + e.getMessage());
                deploymentModel.displayStatus = "Error";
            }
//            }

            byte[] value = deploymentModel.toJson().getBytes(StandardCharsets.UTF_8);

            return connection.store(DEPLOYMENT_PREFIX, key, value);
        });
    }

    public CompletableFuture<Void> updateDeployment(DeploymentActiveModel deployment) throws Exception {
        // TODO: support update
        return this.saveDeployment(deployment);
    }

    public CompletableFuture<DeploymentActiveModel> findDeploymentByUuid(UUID uuid) {
        byte[] key = uuid.toString().getBytes(StandardCharsets.UTF_8);

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