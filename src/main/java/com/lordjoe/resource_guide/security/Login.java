package com.lordjoe.resource_guide.security;

import com.lordjoe.resource_guide.*;
import com.lordjoe.resource_guide.model.AppUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.lordjoe.resource_guide.LocalResourceGuidsApplication;

public class Login {


    public static boolean isUserAuthorized(HttpServletRequest request) {
        if (LocalResourceGuidsApplication.DEBUG_ADMIN_MODE) {
            return true;
        }

        // Your real authorization logic
        Object user = request.getSession().getAttribute("user");
        return user != null && userIsAdmin(user);
    }

    private static boolean userIsAdmin(Object user) {
        // Implement actual user role check
        return true; // Stub for now
    }


    public static boolean isUserLoggedIn(HttpServletRequest request) {
        if (request.getCookies() == null) return false;

        for (Cookie cookie : request.getCookies()) {
            if ("EASTSIDE_LOGIN_TOKEN".equals(cookie.getName())) {
                String token = cookie.getValue();
                AppUser user = Guide.Instance.getUserByUsername(token);
                return user != null;
            }
        }
        return false;
    }

    // Optional: get current user from cookie
    public static AppUser getLoggedInUser(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("EASTSIDE_LOGIN_TOKEN".equals(cookie.getName())) {
                String token = cookie.getValue();
                return Guide.Instance.getUserByUsername(token);
            }
        }
        return null;
    }
}

