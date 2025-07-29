package com.lordjoe.resource_guide.security;

import com.lordjoe.resource_guide.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class Login {

    public static boolean isUserLoggedIn(HttpServletRequest request) {
        if (request.getCookies() == null) return false;

        for (Cookie cookie : request.getCookies()) {
            if ("EASTSIDE_LOGIN_TOKEN".equals(cookie.getName())) {
                String token = cookie.getValue();
                GuideUser user = Guide.Instance.getUserByUsername(token);
                return user != null;
            }
        }
        return false;
    }

    // Optional: get current user from cookie
    public static GuideUser getLoggedInUser(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("EASTSIDE_LOGIN_TOKEN".equals(cookie.getName())) {
                String token = cookie.getValue();
                return  Guide.Instance.getUserByUsername(token);
            }
        }
        return null;
    }
}

