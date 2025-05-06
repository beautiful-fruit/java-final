package io.beautifulfruit.finalproject.view;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.beautifulfruit.finalproject.etcd.user.UserActiveModel;
import io.beautifulfruit.finalproject.etcd.user.UserEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class Login {
    @Autowired
    private UserEntity userEntity;

    @Autowired
    private Validation validation;


    // Display the form for addition
    @PostMapping("/login")
    public String login(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        HttpServletResponse response
    ) {
        UserActiveModel userActiveModel = userEntity.findUserByName(username).join();

        if (userActiveModel == null)
            return "redirect:/";

        System.out.println(userActiveModel.name);
        if (!userActiveModel.passwordMatch(password))
            return "redirect:/";

        Cookie cookie = new Cookie("token", validation.generateToken(username));
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/register")
    public String register(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        HttpServletResponse response
    ) {
        UserActiveModel userActiveModel = userEntity.findUserByName(username).join();

        if (userActiveModel != null)
            return "redirect:/";

        if (!username.matches("[a-zA-Z0-9]+"))
            return "badhacker";

        userEntity.saveUser(new UserActiveModel(username, password));

        return "redirect:/";
    }
}