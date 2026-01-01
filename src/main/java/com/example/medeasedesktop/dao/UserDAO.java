package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.Session;
import com.example.medeasedesktop.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean authenticateAndLoad(String role, String email, String password) {
        String sql = """
            SELECT id, role, email, full_name, enabled, password_hash
            FROM users
            WHERE role = ? AND lower(email) = lower(?)
            LIMIT 1
        """;

        String roleIn = role == null ? "" : role.trim();
        String emailIn = email == null ? "" : email.trim();
        String passIn = password == null ? "" : password.trim();

        System.out.println("---- LOGIN TRY ----");
        System.out.println("role=" + roleIn);
        System.out.println("email=" + emailIn);
        System.out.println("pass=" + passIn);

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, roleIn);
            ps.setString(2, emailIn);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("❌ No user found for role+email");
                    return false;
                }

                int enabled = rs.getInt("enabled");
                String stored = rs.getString("password_hash");
                stored = stored == null ? "" : stored.trim();

                System.out.println("DB row -> id=" + rs.getInt("id")
                        + ", role=" + rs.getString("role")
                        + ", email=" + rs.getString("email")
                        + ", enabled=" + enabled
                        + ", storedPass=" + stored);

                if (enabled != 1) {
                    System.out.println("❌ User is disabled");
                    return false;
                }

                if (!stored.equals(passIn)) {
                    System.out.println("❌ Password mismatch");
                    return false;
                }

                Session.set(
                        rs.getInt("id"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("full_name")
                );

                System.out.println("✅ LOGIN SUCCESS");
                return true;
            }

        } catch (Exception e) {
            System.out.println("❌ LOGIN ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
