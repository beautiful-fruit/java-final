package io.beautifulfruit.finalproject.view;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainPage {

    @Autowired
    Validation validation;

    // Display the form for addition
    @GetMapping("/")
    public String mainpage(HttpServletRequest request) {
        String username = validation.validateTokenFromCookie(request);
        if (username == null)
            return "redirect:/login";
        return "console";
    }
}