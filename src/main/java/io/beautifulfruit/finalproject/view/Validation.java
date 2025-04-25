package io.beautifulfruit.finalproject.view;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;


public class Validation {
    public static String validateTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals("token"))
                .map(Cookie::getValue)
                .findFirst()
                .map(Login::validateToken)
                .orElse(null);
    }
}