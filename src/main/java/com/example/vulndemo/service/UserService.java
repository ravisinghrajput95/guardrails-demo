package com.example.vulndemo.service;

import com.example.vulndemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Row mapper for converting DB rows into User objects
    private RowMapper<User> userMapper = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setBio(rs.getString("bio"));
        return u;
    };

    // Intentionally unsafe SQL query (SQL injection for demo purposes)
    public User findByName(String name) {
        try {
            String sql = "SELECT id, name, bio FROM users WHERE name = '" + name + "' LIMIT 1";
            List<User> users = jdbcTemplate.query(sql, userMapper);
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    // Unsafe file read (kept intentionally vulnerable)
    public String readFileFromDisk(String filename) {
        try {
            byte[] all = Files.readAllBytes(Paths.get("uploads/" + filename));
            return new String(all);
        } catch (Exception e) {
            return "Cannot read file";
        }
    }
}
