package com.lordjoe.resource_guide.display;

public class LoginPageGenerator {

    public static String generateLoginPage(String csrfParam, String csrfValue, String message) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<meta charset='utf-8'>\n");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1'>\n");
        html.append("<title>Login - Eastside Resource Guide</title>\n");
        html.append("<link rel=\"icon\" type=\"image/x-icon\" href=\"/favicon.ico\">\n");

        html.append("<style>\n");
        html.append("body { background-image:url('/Cover.png'); background-size:cover; background-position:center; ");
        html.append("font-family:Arial,sans-serif; display:flex; justify-content:center; align-items:center; height:100vh; margin:0; }\n");
        html.append(".login-box { background:rgba(255,255,255,0.95); padding:40px; border-radius:12px; box-shadow:0 0 10px rgba(0,0,0,0.3); text-align:center; width:300px; }\n");
        html.append(".login-box h2 { margin-bottom:20px; }\n");
        html.append(".login-box label { display:block; text-align:left; font-weight:bold; margin:10px 0 6px; }\n");
        html.append(".login-box input { width:100%; padding:10px; margin:0 0 10px 0; border:1px solid #ccc; border-radius:6px; font-size:16px; }\n");
        html.append(".login-box button { padding:10px 20px; font-size:16px; background-color:#4CAF50; color:#fff; border:0; border-radius:6px; cursor:pointer; }\n");
        html.append(".error-msg { color:red; margin-bottom:10px; }\n");
        html.append(".logout-msg { color:green; margin-bottom:10px; }\n");
        html.append(".forgot-password { margin-top:30px; font-size:14px; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<div class='login-box'>\n");
        html.append("<h2>Login to Eastside Resource Guide</h2>\n");

        if (message != null && !message.isBlank()) {
            boolean isError = message.toLowerCase().contains("invalid") || message.toLowerCase().contains("error");
            html.append("<div class='").append(isError ? "error-msg" : "logout-msg").append("'>")
                    .append(escape(message)).append("</div>\n");
        }

        // LOGIN FORM — use standard names, labels, ids, and autocomplete hints
        html.append("<form id='loginForm' method='post' action='/login' autocomplete='on'>\n");
        if (csrfParam != null && csrfValue != null) {
            html.append("  <input type='hidden' name='").append(escape(csrfParam)).append("' value='")
                    .append(escape(csrfValue)).append("'>\n");
        }
        html.append("  <label for='username'>Email</label>\n");
        html.append("  <input type='email' id='username' name='username' inputmode='email' ")
                .append("autocomplete='username' placeholder='you@example.com' required autofocus>\n");
        html.append("  <label for='password'>Password</label>\n");
        html.append("  <input type='password' id='password' name='password' ")
                .append("autocomplete='current-password' placeholder='Your password' required>\n");
        html.append("  <button type='submit'>Login</button>\n");
        html.append("</form>\n");

        // FORGOT PASSWORD — separate form; won’t confuse PMs (no password field)
        html.append("<div class='forgot-password'>\n");
        html.append("  <form method='post' action='/forgot-password' autocomplete='on'>\n");
        if (csrfParam != null && csrfValue != null) {
            html.append("    <input type='hidden' name='").append(escape(csrfParam)).append("' value='")
                    .append(escape(csrfValue)).append("'>\n");
        }
        html.append("    <label for='fp-email'>Forgot your password?</label>\n");
        html.append("    <input type='email' id='fp-email' name='email' inputmode='email' autocomplete='email' ")
                .append("placeholder='Enter your email' required>\n");
        html.append("    <button type='submit'>Reset Password</button>\n");
        html.append("  </form>\n");
        html.append("</div>\n");

        html.append("</div>\n");
        html.append("</body>\n</html>");
        return html.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
