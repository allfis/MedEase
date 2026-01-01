package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.Session;
import com.example.medeasedesktop.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean authenticateAndLoad(String role, String email, String password) {

        if (role == null || email == null || password == null) return false;

        String sql = """
            SELECT id, role, email, full_name, enabled, password_hash
            FROM users
            WHERE role=? AND lower(email)=lower(?)
            LIMIT 1
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, role.trim().toUpperCase());
            ps.setString(2, email.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return false;
                if (rs.getInt("enabled") != 1) return false;

                String stored = rs.getString("password_hash");
                if (stored == null) stored = "";

                String given = password.trim();

                if (!stored.equals(given)) return false;

                Session.set(
                        rs.getInt("id"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("full_name")
                );
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
