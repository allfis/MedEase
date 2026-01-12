package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.Session;
import com.example.medeasedesktop.db.DB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean authenticateAndLoad(String role, String email, String password) {

        String sql = """
            SELECT id, role, email, password_hash, full_name, enabled
            FROM users
            WHERE email = ?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                int id = rs.getInt("id");
                String dbRole = rs.getString("role");
                String dbEmail = rs.getString("email");
                String hash = rs.getString("password_hash");
                String name = rs.getString("full_name");
                int enabled = rs.getInt("enabled");

                if (enabled != 1) return false;

                if (dbRole == null || !dbRole.equalsIgnoreCase(role)) return false;

                if (hash == null || hash.isBlank()) return false;
                if (!BCrypt.checkpw(password, hash)) return false;

                Session.set(id, dbRole, dbEmail, name);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
