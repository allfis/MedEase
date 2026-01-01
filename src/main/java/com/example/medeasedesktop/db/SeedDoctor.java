package com.example.medeasedesktop.db;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SeedDoctor {
    public static void main(String[] args) throws Exception {
        Schema.init();

        String sql = "INSERT OR IGNORE INTO users(role,email,password_hash,full_name) VALUES(?,?,?,?)";

        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "DOCTOR");
            ps.setString(2, "doctor@medease.com");
            ps.setString(3, BCrypt.hashpw("doctor123", BCrypt.gensalt()));
            ps.setString(4, "Dr. Test");
            ps.executeUpdate();
        }
    }
}
