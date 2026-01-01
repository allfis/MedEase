package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.DoctorRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorManagementDAO {

    public List<DoctorRow> listDoctors(String q, String statusFilter) {
        String base = """
            SELECT id, full_name, email, enabled
            FROM users
            WHERE role='DOCTOR'
        """;

        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder(base);

        if (q != null && !q.isBlank()) {
            sb.append(" AND (LOWER(full_name) LIKE ? OR LOWER(email) LIKE ?)");
            String like = "%" + q.toLowerCase().trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (statusFilter != null && !statusFilter.isBlank() && !"All".equals(statusFilter)) {
            if ("Enabled".equals(statusFilter)) sb.append(" AND enabled=1");
            if ("Disabled".equals(statusFilter)) sb.append(" AND enabled=0");
        }

        sb.append(" ORDER BY id DESC");

        List<DoctorRow> out = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int enabled = rs.getInt("enabled");
                    out.add(new DoctorRow(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            enabled == 1 ? "Enabled" : "Disabled"
                    ));
                }
            }
            return out;
        } catch (Exception e) {
            return out;
        }
    }

    public boolean addDoctor(String fullName, String email, String password) {
        String insertUser = "INSERT INTO users(role,email,password_hash,full_name,enabled) VALUES('DOCTOR',?,?,?,1)";
        String insertProfile = "INSERT OR IGNORE INTO doctor_profile(user_id) VALUES(?)";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, email);
                ps.setString(2, password == null ? "" : password);
                ps.setString(3, fullName);
                ps.executeUpdate();

                int newId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        c.rollback();
                        c.setAutoCommit(true);
                        return false;
                    }
                    newId = keys.getInt(1);
                }

                try (PreparedStatement ps2 = c.prepareStatement(insertProfile)) {
                    ps2.setInt(1, newId);
                    ps2.executeUpdate();
                }

                c.commit();
                c.setAutoCommit(true);
                return true;

            } catch (Exception ex) {
                c.rollback();
                c.setAutoCommit(true);
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean setEnabled(int doctorId, boolean enabled) {
        String sql = "UPDATE users SET enabled=? WHERE id=? AND role='DOCTOR'";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enabled ? 1 : 0);
            ps.setInt(2, doctorId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeDoctor(int doctorId) {
        String sql = "DELETE FROM users WHERE id=? AND role='DOCTOR'";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            return false;
        }
    }
}
