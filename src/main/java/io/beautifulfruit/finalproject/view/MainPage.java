package io.beautifulfruit.finalproject.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPage {

    // Display the form for addition
    @GetMapping("/")
    public String showAddForm() {
        return "main";
    }
}