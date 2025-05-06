package io.beautifulfruit.finalproject.view;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.beautifulfruit.finalproject.etcd.deployment.DeploymentActiveModel;
import io.beautifulfruit.finalproject.etcd.deployment.DeploymentEntity;
import io.beautifulfruit.finalproject.etcd.user.UserActiveModel;
import io.beautifulfruit.finalproject.etcd.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;



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
        ){
        String username = validation.validateTokenFromCookie(request);

        if (username == null)
            return "fail";

        UserActiveModel userActiveModel = userEntity.findUserByName(username).join();
        String uuid = body.get("uuid");
        // TODO: Delete deployment by UUID
        System.out.println("Delete deployment by UUID: " + uuid);
        return "";
    }
}