package com.lordjoe.resource_guide.display;

public class LoginPageGenerator {

    public static String generateLoginPage(boolean showError) {
        StringBuilder html = new StringBuilder();
        html.append("""
        <html>
        <head>
            <title>Login</title>
            <link rel="icon" type="image/x-icon" href="/favicon.ico">
            <style>
                body {
                    background-image: url('/Cover.png');
                    background-size: cover;
                    background-position: center;
                    font-family: Arial, sans-serif;
                    margin: 0;
                    padding: 20px;
                }
                .login-container {
                    max-width: 400px;
                    margin: 100px auto;
                    padding: 30px;
                    background-color: rgba(255, 255, 255, 0.95);
                    border-radius: 12px;
                    box-shadow: 0 4px 8px rgba(0,0,0,0.2);
                }
                h2 {
                    text-align: center;
                    color: #333;
                }
                .error-message {
                    color: red;
                    text-align: center;
                    font-weight: bold;
                    margin-bottom: 10px;
                }
                label {
                    display: block;
                    margin-top: 15px;
                    font-weight: bold;
                    color: #333;
                }
                input[type='text'],
                input[type='password'] {
                    width: 100%;
                    padding: 10px;
                    margin-top: 5px;
                    border: 1px solid #ccc;
                    border-radius: 6px;
                    box-sizing: border-box;
                }
                button {
                    width: 100%;
                    margin-top: 20px;
                    padding: 12px;
                    background-color: #3f51b5;
                    color: white;
                    font-size: 16px;
                    border: none;
                    border-radius: 6px;
                    cursor: pointer;
                    transition: background-color 0.3s;
                }
                button:hover {
                    background-color: #2c3f91;
                }
            </style>
        </head>
        <body>
            <div class="login-container">
                <h2>Admin Login</h2>
        """);

        if (showError) {
            html.append("<div class='error-message'>Invalid username or password</div>");
        }

        html.append("""
                <form method="post" action="/login">
                    <label for="username">Username:</label>
                    <input type="text" name="username" id="username" required />

                    <label for="password">Password:</label>
                    <input type="password" name="password" id="password" required />

                    <button type="submit">Login</button>
                </form>
            </div>
        </body>
        </html>
        """);

        return html.toString();
    }
}
