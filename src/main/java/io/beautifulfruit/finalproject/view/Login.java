package io.beautifulfruit.finalproject.view;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.beautifulfruit.finalproject.etcd.user.UserActiveModel;
import io.beautifulfruit.finalproject.etcd.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class Login {
    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final long EXPIRATION = 3600000;

    @Autowired
    private UserEntity userEntity;


    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public static String validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

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

        Cookie cookie = new Cookie("token", generateToken(username));
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

        userEntity.saveUser(new UserActiveModel(username, password));

        return "redirect:/";
    }
}