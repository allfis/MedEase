package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.DoctorProfileRow;
import com.example.medeasedesktop.model.PatientDoctorRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PatientDoctorDAO {

    public List<PatientDoctorRow> searchDoctors(String q) {

        String base = """
            SELECT u.id AS doctor_id,
                   COALESCE(u.full_name,'') AS name,
                   u.email AS email,
                   COALESCE(d.specialty,'') AS specialty,
                   COALESCE(d.about,'') AS hospital
            FROM users u
            LEFT JOIN doctor_profile d ON d.user_id = u.id
            WHERE u.role='DOCTOR' AND u.enabled=1
        """;

        StringBuilder sb = new StringBuilder(base);
        List<Object> params = new ArrayList<>();

        if (q != null && !q.trim().isBlank()) {
            String like = "%" + q.trim().toLowerCase() + "%";
            sb.append("""
                AND (
                    LOWER(COALESCE(u.full_name,'')) LIKE ?
                    OR LOWER(COALESCE(u.email,'')) LIKE ?
                    OR LOWER(COALESCE(d.specialty,'')) LIKE ?
                    OR LOWER(COALESCE(d.about,'')) LIKE ?
                )
            """);
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }

        sb.append(" ORDER BY COALESCE(u.full_name,''), u.id DESC");

        List<PatientDoctorRow> out = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new PatientDoctorRow(
                            rs.getInt("doctor_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("specialty"),
                            rs.getString("hospital")
                    ));
                }
            }

        } catch (Exception e) {
            return out;
        }

        return out;
    }

    public DoctorProfileRow getDoctorProfile(int doctorId) {

        String sql = """
            SELECT u.id AS id,
                   COALESCE(u.full_name,'') AS full_name,
                   u.email AS email,
                   COALESCE(d.specialty,'') AS specialty,
                   COALESCE(d.about,'') AS about
            FROM users u
            LEFT JOIN doctor_profile d ON d.user_id = u.id
            WHERE u.id = ? AND u.role='DOCTOR' AND u.enabled=1
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new DoctorProfileRow(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("specialty"),
                        rs.getString("about")
                );
            }

        } catch (Exception e) {
            return null;
        }
    }



    private String mapDay(DayOfWeek d) {
        return switch (d) {
            case SUNDAY -> "Sun";
            case MONDAY -> "Mon";
            case TUESDAY -> "Tue";
            case WEDNESDAY -> "Wed";
            case THURSDAY -> "Thu";
            case FRIDAY -> "Fri";
            case SATURDAY -> "Sat";
        };
    }
}
