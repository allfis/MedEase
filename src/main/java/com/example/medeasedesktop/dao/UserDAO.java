package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.Session;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean authenticateAndLoad(String role, String email, String password) {

        String sql = """
        SELECT id, password_hash, full_name
        FROM users
        WHERE role=? AND email=? AND enabled=1
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, role);
            ps.setString(2, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                String hash = rs.getString("password_hash");
                if (!BCrypt.checkpw(password, hash)) return false;

                Session.set(
                        rs.getInt("id"),
                        role,
                        email,
                        rs.getString("full_name")
                );
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }

}
