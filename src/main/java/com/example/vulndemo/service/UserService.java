package com.example.vulndemo.service;

import org.springframework.stereotype.Service;
import java.sql.*;
import com.example.vulndemo.model.User;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class UserService {
    private Connection conn;

    public UserService() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:data/demo.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Intentionally unsafe: builds SQL via string concatenation (SQL injection)
    public User findByName(String name) {
        try {
            String sql = "SELECT id, name, bio FROM users WHERE name = '" + name + "' LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("name"), rs.getString("bio"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Danger: reads file path from DB and returns content (demonstrates file read risk)
    public String readFileFromDisk(String filename) {
        try {
            byte[] all = Files.readAllBytes(Paths.get("uploads/" + filename));
            return new String(all);
        } catch (Exception e) {
            return "Cannot read file";
        }
    }
}
