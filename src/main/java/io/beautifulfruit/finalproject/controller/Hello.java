package io.beautifulfruit.finalproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Hello {

    @GetMapping("/hello")
    public String hello(Model model) {
        // Adding an attribute to the model that will be accessible in the Thymeleaf template
        model.addAttribute("message", "Hello, World!");
        // Returning the name of the Thymeleaf template (index.html)
        return "hello";
    }
}