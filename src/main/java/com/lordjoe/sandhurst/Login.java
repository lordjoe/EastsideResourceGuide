package com.lordjoe.sandhurst;

import jakarta.servlet.http.HttpServletRequest;

/**
 * com.lordjoe.sandhurst.Login
 * User: Steve
 * Date: 6/26/25
 */


public class Login {
    // For now, this just assumes the user is always logged in and authorized
    public static boolean isAuthorized() {
        return true;
    }

    public static boolean isUserAuthorized(HttpServletRequest request) {
        return isAuthorized();
    }

    // In the future, this can check cookies, sessions, or JWTs
}

