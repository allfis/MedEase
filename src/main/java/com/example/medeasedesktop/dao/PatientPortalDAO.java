package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PatientPortalDAO {

    public record DoctorItem(int id, String name, String email, String specialty, String phone) {}

    public record MyApptItem(int id, String date, String time, String doctor, String status) {}

    public List<DoctorItem> searchDoctors(String q) {
        String like = "%" + (q == null ? "" : q.trim().toLowerCase()) + "%";

        String sql = """
            SELECT u.id,
                   COALESCE(u.full_name,'Doctor') AS name,
                   u.email,
                   COALESCE(d.specialty,'') AS specialty,
                   COALESCE(d.phone,'') AS phone
            FROM users u
            LEFT JOIN doctor_profile d ON d.user_id=u.id
            WHERE u.role='DOCTOR' AND u.enabled=1
              AND (LOWER(u.full_name) LIKE ? OR LOWER(u.email) LIKE ? OR LOWER(d.specialty) LIKE ? OR LOWER(d.phone) LIKE ?)
            ORDER BY u.full_name
        """;

        List<DoctorItem> out = new ArrayList<>();
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new DoctorItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("specialty"),
                            rs.getString("phone")
                    ));
                }
            }
        } catch (Exception e) {
            return out;
        }
        return out;
    }

    public List<DoctorItem> emergencyDoctors() {
        String sql = """
            SELECT u.id,
                   COALESCE(u.full_name,'Doctor') AS name,
                   u.email,
                   COALESCE(d.specialty,'') AS specialty,
                   COALESCE(d.phone,'') AS phone
            FROM users u
            LEFT JOIN doctor_profile d ON d.user_id=u.id
            WHERE u.role='DOCTOR' AND u.enabled=1
            ORDER BY u.full_name
            LIMIT 30
        """;

        List<DoctorItem> out = new ArrayList<>();
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new DoctorItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("specialty"),
                        rs.getString("phone")
                ));
            }
        } catch (Exception e) {
            return out;
        }
        return out;
    }

    public List<MyApptItem> myAppointments(int patientId) {
        String sql = """
            SELECT a.id,
                   a.appt_date AS appt_date,
                   a.appt_time AS appt_time,
                   COALESCE(u.full_name,'Unassigned') AS doctor,
                   a.status AS status
            FROM appointments a
            LEFT JOIN users u ON u.id=a.doctor_id
            WHERE a.patient_id=?
            ORDER BY a.id DESC
        """;

        List<MyApptItem> out = new ArrayList<>();
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new MyApptItem(
                            rs.getInt("id"),
                            rs.getString("appt_date"),
                            rs.getString("appt_time"),
                            rs.getString("doctor"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            return out;
        }
        return out;
    }
}
