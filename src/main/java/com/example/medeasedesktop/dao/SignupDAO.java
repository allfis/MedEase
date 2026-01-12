package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;

import java.sql.*;

public class SignupDAO {

    public Integer createUser(String role, String email, String password, String fullName) {
        String sql = "INSERT INTO users(role,email,password_hash,full_name,enabled) VALUES(?,?,?,?,1)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, role == null ? "PATIENT" : role.trim());
            ps.setString(2, email == null ? "" : email.trim());
            ps.setString(3, password == null ? "" : password);
            ps.setString(4, fullName == null ? "" : fullName.trim());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) return null;
                return rs.getInt(1);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
