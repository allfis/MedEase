package com.example.medeasedesktop.db;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SeedAdmin {
    public static void main(String[] args) throws Exception {
        Schema.init();

        String sql = "INSERT OR IGNORE INTO users(role,email,password_hash,full_name) VALUES(?,?,?,?)";

        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "ADMIN");
            ps.setString(2, "admin@medease.com");
            ps.setString(3, BCrypt.hashpw("admin123", BCrypt.gensalt()));
            ps.setString(4, "System Admin");
            ps.executeUpdate();
        }
    }
}
