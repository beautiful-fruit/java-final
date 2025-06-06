package io.beautifulfruit.finalproject.view;

import io.beautifulfruit.finalproject.etcd.deployment.DeploymentActiveModel;
import io.beautifulfruit.finalproject.etcd.deployment.DeploymentEntity;
import io.beautifulfruit.finalproject.etcd.user.UserActiveModel;
import io.beautifulfruit.finalproject.etcd.user.UserEntity;
import io.beautifulfruit.finalproject.k8s.Quota;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
public class Upload {
    String content;
    String uploadStatus = "No file";

    @Autowired
    private UserEntity userEntity;

    @Autowired
    private Validation validation;

    @Autowired
    private DeploymentEntity deploymentEntity;

    @PostMapping("/upload")
    public String handleUpload(
            @RequestBody Map<String, String> body,
            Model model,
            HttpServletRequest request
    ) {
        String username = validation.validateTokenFromCookie(request);

        if (username == null)
            return "fail";

        UserActiveModel userActiveModel = userEntity.findUserByName(username).join();

        if (userActiveModel == null)
            return "fail";

        String file = body.get("file");
        if (file == null || file.isEmpty())
            return "fail";

        int cpu, memory, disk;
        System.out.println(body.get("cpu") + " " + body.get("memory") + " " + body.get("disk"));
        try {
            cpu = Integer.parseInt(body.get("cpu"));
            memory = Integer.parseInt(body.get("memory"));
            disk = Integer.parseInt(body.get("disk"));
        } catch (Exception e) {
            return e.toString();
        }
        if (cpu > userActiveModel.quota.cpu
                || memory > userActiveModel.quota.memory
                || disk > userActiveModel.quota.disk
        ) {
            return "Out of resource";
        }
        userActiveModel.quota.cpu -= cpu;
        userActiveModel.quota.memory -= memory;
        userActiveModel.quota.disk -= disk;
        DeploymentActiveModel deploymentActiveModel =
                new DeploymentActiveModel(file, userActiveModel.name, new Quota(memory, cpu, disk));
        try {
            deploymentEntity.updateDeployment(deploymentActiveModel).join();
            userActiveModel.addDeploymentID(deploymentActiveModel.uuid);
            userEntity.saveUser(userActiveModel).join();
        } catch (Exception e) {
            System.out.println(e);
            // TODO: User remove the deployment
            return e.toString();
        }
        System.out.println("success");
        uploadStatus = "Success";
        return "success";
    }

    @GetMapping("/upload_status")
    public Map<String, Object> getUploadStatus(HttpServletRequest request) {
        String username = validation.validateTokenFromCookie(request);

        if (username == null)
            return null;
        UserActiveModel userActiveModel = userEntity.findUserByName(username).join();

        if (userActiveModel == null)
            return null;

        ArrayList<UUID> uuids = userActiveModel.getOwnDeploymentID();
        String dockerCompose = "";
        Map<String, Object> informations = new HashMap<>();
        List<Map<String, Object>> containers = new ArrayList<>();

        for (UUID uuid : uuids) {
            DeploymentActiveModel deploymentActiveModel =
                    deploymentEntity.findDeploymentByUuid(uuid).join();
            Map<String, Object> container = new HashMap<>();
            container.put("uuid", uuid.toString());
            container.put("cpu", deploymentActiveModel.quota.cpu);
            container.put("memory", deploymentActiveModel.quota.memory);
            container.put("disk", deploymentActiveModel.quota.disk);
            containers.add(container);
        }
        informations.put("containers", containers);
        informations.put("username", userActiveModel.name);
        informations.put("cpu", userActiveModel.quota.cpu);
        informations.put("memory", userActiveModel.quota.memory);
        informations.put("disk", userActiveModel.quota.disk);
        return informations;
    }
}