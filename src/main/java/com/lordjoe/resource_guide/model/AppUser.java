package com.lordjoe.resource_guide.model;

import com.lordjoe.resource_guide.util.DatabaseConnection;
import com.lordjoe.utilities.EmailSender;
import com.lordjoe.utilities.Encrypt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lordjoe.resource_guide.Database.createDatabase;

public class AppUser {
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String encryptedPassword;


    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public AppUser(String email,String firstname, String lastname,  String encryptedPassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    private static void insertUser(String email, String password, String firstName, String lastName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT INTO users (email, password, first_name, last_name)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (email) DO NOTHING
        """);
            stmt.setString(1, email);
            stmt.setString(2, password); // Should be hashed in production
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.executeUpdate();
        }
    }

    public static boolean validateUser(String email, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password); // Compare hashed version in production
            }
            return false;
        }
    }

    public static void sendPasswordRecoveryEmail(String email) throws SQLException {
        String sql = "SELECT password, first_name FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String password = rs.getString("password");
                String firstName = rs.getString("first_name");

                String message = String.format("Hi %s,\n\nYour password is: %s\n\nRegards,\nSupport Team",
                        firstName != null ? firstName : "User", password);

                EmailSender.send(email, "Password Recovery", message);
            } else {
                String message = "Eastside Resource has no email for you";
                EmailSender.send(email, "Password Recovery", message);
                throw new RuntimeException("Email not found.");
            }
        }
    }


        public static void loadUsersFromCSV(String filePath) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath));
                 Connection conn = DatabaseConnection.getConnection()) {

                String line = br.readLine(); // Skip header
                if (line == null || !line.toLowerCase().contains("email")) {
                    throw new RuntimeException("CSV header must contain 'email'");
                }

                String insertSQL = """
                INSERT INTO users (email, password, first_name, last_name)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (email) DO NOTHING
            """;

                PreparedStatement stmt = conn.prepareStatement(insertSQL);

                while ((line = br.readLine()) != null) {
                    String[] fields = line.split("\t", -1); // keep empty fields
                    if (fields.length < 4) {
                        System.err.println("Skipping invalid line: " + line);
                        continue;
                    }

                    String email = fields[2].trim();
                    String pw = fields[3].trim();
                    pw = Encrypt.encryptString(pw) ;
                    String password = pw;  // optionally hash this
                    String firstName = fields[0].trim();
                    String lastName = fields[1].trim();

                    stmt.setString(1, email);
                    stmt.setString(2, password);
                    stmt.setString(3, firstName);
                    stmt.setString(4, lastName);
                    stmt.addBatch();
                }

                stmt.executeBatch();
                System.out.println("Users loaded successfully from " + filePath);

            } catch (Exception e) {
                throw new RuntimeException("Failed to load users from CSV", e);
            }
        }

    public static void dropUsers() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Resetting database tables...");
            // old
                 stmt.executeUpdate("DROP TABLE IF EXISTS users CASCADE");


            System.out.println("Users dropped.");
            DatabaseConnection.clearConnection();
        }
    }

    public static List<AppUser> LoadUsers() {
        List<AppUser> ret = new ArrayList<AppUser>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT email, password, first_name,last_name" +
                     " from users")) {

            while (rs.next()) {
                String username = rs.getString("email");
                String encryptedPassword = rs.getString("password");
                String firat = rs.getString("first_name");
                String last = rs.getString("last_name");
                AppUser user = new AppUser(username, firat, last, encryptedPassword);
                ret.add(user);
            }
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load users", e);
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFirstname()) ;
        sb.append(", ");
        sb.append(getLastname()) ;
        sb.append(", ");
        sb.append(getEmail()) ;
        sb.append(", ");
        sb.append(getEncryptedPassword());
        return sb.toString();
    }

    public void sendPasswordEmail()
    {
        String address =  getEmail();
        String password = Encrypt.decryptString(getEncryptedPassword());
        String subject = "Password to Eastside Resource";
        String message = "Here is your password to Eastside Resourse \""  +  password + "\"";
        EmailSender.send(address, subject, message);
    }

    public static void main(String[] args) throws Exception {
        // for noe add users to reoote
       // DatabaseConnection.setREMOTE();
        dropUsers();
        createDatabase();
        loadUsersFromCSV(args[0]);
        List<AppUser> users = LoadUsers();
        Map<String, AppUser> map = new HashMap<String, AppUser>();
        for (AppUser user : users) {
            System.out.println(user);
            map.put(user.getEmail().toLowerCase(), user);
        }
        AppUser user = map.get("lordjoe2000@gmail.com");
        user.sendPasswordEmail();
    }
}

