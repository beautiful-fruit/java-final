package io.beautifulfruit.finalproject.view;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.beautifulfruit.finalproject.etcd.user.UserActiveModel;
import io.beautifulfruit.finalproject.etcd.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;



@RestController
public class Upload {
    String content;
    String uploadStatus = "No file";

    @Autowired
    private UserEntity userEntity;

    @PostMapping("/upload")
    public String handleUpload(
        @RequestBody Map<String, String> body,
        Model model,
        HttpServletRequest request
        ){
        String username = Validation.validateTokenFromCookie(request);

        if (username == null)
            return "fail";

        UserActiveModel userActiveModel = userEntity.findUserByName(username).join();

        if (userActiveModel == null)
            return "fail";

        String file = body.get("file");

        if (file.length() == 0)
            return "fail";

        // TODO: add container
        uploadStatus = "Success";
        return "success";
    }

    @GetMapping("/upload_status")
    public String getUploadStatus(HttpServletRequest request) {
        String username = Validation.validateTokenFromCookie(request);

        if (username == null)
            return "fail";

        UserActiveModel userActiveModel = userEntity.findUserByName(username).join();

        if (userActiveModel == null)
            return "fail";

        // TODO: return container message
        return uploadStatus;
    }
}