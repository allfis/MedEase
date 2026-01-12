package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PatientDAO {

    public Integer findPatientIdByUserId(int userId) {
        String sql = "SELECT id FROM patients WHERE user_id=? LIMIT 1";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getInt("id");
            }
        } catch (Exception e) {
            return null;
        }
    }

    public boolean createPatientRow(int userId, String fullName, String email, String phone) {
        String sql = """
            INSERT INTO patients(user_id, full_name, email, phone)
            VALUES(?,?,?,?)
        """;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, fullName == null ? "" : fullName.trim());
            ps.setString(3, email == null ? "" : email.trim());
            ps.setString(4, phone == null ? "" : phone.trim());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            return false;
        }
    }
}
