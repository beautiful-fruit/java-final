package io.beautifulfruit.finalproject.controller;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Upload {
    String content;
    String uploadStatus = "No file";
    User testUser = new User(1, 100);

    @PostMapping("/upload")
    public String handleUpload(@RequestBody Map<String, String> body, Model model) {
        String file = body.get("file");

        if (file.length() == 0)
            return "fail";
        testUser.addContainer(file);
        uploadStatus = "Success";
        return "success";
    }

    @GetMapping("/upload_status")
    public String getUploadStatus() {
        if (testUser.getContainerNumber() != 0)
            return testUser.getContainer(testUser.getContainerNumber() - 1).dockerCompose;
        return uploadStatus;
    }
}