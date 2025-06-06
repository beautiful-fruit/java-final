package io.beautifulfruit.finalproject.view;

import io.beautifulfruit.finalproject.etcd.deployment.DeploymentActiveModel;
import io.beautifulfruit.finalproject.etcd.deployment.DeploymentEntity;
import io.beautifulfruit.finalproject.etcd.user.UserActiveModel;
import io.beautifulfruit.finalproject.etcd.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;


@RestController
public class Delete {

    @Autowired
    private UserEntity userEntity;

    @Autowired
    private Validation validation;

    @Autowired
    private DeploymentEntity deploymentEntity;

    @PostMapping("/delete")
    public String handleUpload(
            @RequestBody Map<String, String> body,
            Model model,
            HttpServletRequest request
    ) {
        String username = validation.validateTokenFromCookie(request);

        if (username == null)
            return "fail";

        UserActiveModel userActiveModel = userEntity.findUserByName(username).join();

        UUID uuid;
        try {
            uuid = UUID.fromString(body.get("uuid"));
        } catch (Exception e) {
            return "fail";
        }

        if (!userActiveModel.containsDeploymentID(uuid))
            return "fail";

        userActiveModel.removeDeploymentID(uuid);
        DeploymentActiveModel deploymentActiveModel =
                deploymentEntity.findDeploymentByUuid(uuid).join();
        if (deploymentActiveModel == null)
            return "fail";
        try {
            /*  TODO: There is a possible error during the period
                between deployment being deployed and user being
                saved. If the uuid is queried in this period, there
                will be a uuid doesn't has the respective deployment.
             */
            deploymentEntity.deleteDeployment(deploymentActiveModel);
            userActiveModel.quota.cpu += deploymentActiveModel.quota.cpu;
            userActiveModel.quota.memory += deploymentActiveModel.quota.memory;
            userActiveModel.quota.disk += deploymentActiveModel.quota.disk;
            // This is implemented with non-blocking
            userEntity.saveUser(userActiveModel);
        } catch (Exception e) {
            return "fail";
        }
        System.out.println("Delete deployment by UUID: " + uuid);
        return "success";
    }
}