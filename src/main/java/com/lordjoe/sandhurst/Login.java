package com.lordjoe.sandhurst;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class Login {
    public static boolean isUserAuthorized(HttpServletRequest request) {
        if (request.getCookies() == null) return false;

        for (Cookie cookie : request.getCookies()) {
            if ("EASTSIDE_LOGIN_TOKEN".equals(cookie.getName())) {
                // Optional: validate token value if needed
                return true;
            }
        }
        return false;
    }
}

    // In the future, this can check cookies, sessions, or JWTs


