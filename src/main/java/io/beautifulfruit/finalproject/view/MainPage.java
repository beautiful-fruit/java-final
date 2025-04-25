package io.beautifulfruit.finalproject.view;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class MainPage {

    // Display the form for addition
    @GetMapping("/")
    public String mainpage(HttpServletRequest request) {
        String username = Validation.validateTokenFromCookie(request);
        if (username == null)
            return "redirect:/login";
        return "upload";
    }
}