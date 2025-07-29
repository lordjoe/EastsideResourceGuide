package com.lordjoe.resource_guide.display;

public class LoginPageGenerator {

    public static String generateLoginPage(boolean showError, boolean showLogout) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<title>Login - Eastside Resource Guide</title>\n");
        html.append("<link rel=\"icon\" type=\"image/x-icon\" href=\"/favicon.ico\">\n");
        html.append("<style>\n");
        html.append("body {\n");
        html.append("  background-image: url('/Cover.png');\n");
        html.append("  background-size: cover;\n");
        html.append("  background-position: center;\n");
        html.append("  font-family: Arial, sans-serif;\n");
        html.append("  display: flex;\n");
        html.append("  justify-content: center;\n");
        html.append("  align-items: center;\n");
        html.append("  height: 100vh;\n");
        html.append("  margin: 0;\n");
        html.append("}\n");
        html.append(".login-box {\n");
        html.append("  background: rgba(255, 255, 255, 0.95);\n");
        html.append("  padding: 40px;\n");
        html.append("  border-radius: 12px;\n");
        html.append("  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);\n");
        html.append("  text-align: center;\n");
        html.append("}\n");
        html.append(".login-box h2 { margin-bottom: 20px; }\n");
        html.append(".login-box input {\n");
        html.append("  width: 100%;\n");
        html.append("  padding: 10px;\n");
        html.append("  margin: 10px 0;\n");
        html.append("  border: 1px solid #ccc;\n");
        html.append("  border-radius: 6px;\n");
        html.append("  font-size: 16px;\n");
        html.append("}\n");
        html.append(".login-box button {\n");
        html.append("  padding: 10px 20px;\n");
        html.append("  font-size: 16px;\n");
        html.append("  background-color: #4CAF50;\n");
        html.append("  color: white;\n");
        html.append("  border: none;\n");
        html.append("  border-radius: 6px;\n");
        html.append("  cursor: pointer;\n");
        html.append("}\n");
        html.append(".error-msg { color: red; margin-bottom: 10px; }\n");
        html.append(".logout-msg { color: green; margin-bottom: 10px; }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class='login-box'>\n");
        html.append("<h2>Login to Eastside Resource Guide</h2>\n");

        if (showError) {
            html.append("<div class='error-msg'>Invalid username or password</div>\n");
        }
        if (showLogout) {
            html.append("<div class='logout-msg'>You have been logged out</div>\n");
        }

        html.append("<form method='post' action='/login'>\n");
        html.append("  <input type='text' name='username' placeholder='Username' value='admin' required>\n");
        html.append("  <input type='password' name='password' placeholder='Password' value='Sulphur32' required>\n");
        html.append("  <button type='submit'>Login</button>\n");
        html.append("</form>\n</div>\n");

        html.append("</body>\n</html>");

        return html.toString();
    }
}
