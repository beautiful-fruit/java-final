package io.beautifulfruit.finalproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Add {

    // Display the form for addition
    @GetMapping("/add")
    public String showAddForm() {
        return "add";
    }

    // Process the form submission
    @PostMapping("/add")
    public String processAdd(
            @RequestParam("firstNumber") int firstNumber,
            @RequestParam("secondNumber") int secondNumber,
            Model model) {
        int result = firstNumber + secondNumber;
        model.addAttribute("result", result);
        return "add-result";
    }
}