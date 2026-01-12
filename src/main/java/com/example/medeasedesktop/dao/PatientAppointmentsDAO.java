package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.PatientAppointmentRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentsDAO {

    public boolean book(int patientId, int doctorId, String date, String time) {

        String sql = """
        INSERT INTO appointments(patient_id, doctor_id, appt_date, appt_time, status, created_at, updated_at)
        VALUES(?,?,?,?, 'PENDING', datetime('now'), datetime('now'))
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, date);
            ps.setString(4, time);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            return false;
        }
    }


    public List<PatientAppointmentRow> listMyAppointments(int userId) {

        String sql = """
            SELECT a.appt_date AS appt_date,
                   a.appt_time AS appt_time,
                   COALESCE(u.full_name,'Unassigned') AS doctor,
                   a.status AS status
            FROM appointments a
            JOIN patients p ON p.id=a.patient_id
            LEFT JOIN users u ON u.id=a.doctor_id
            WHERE p.user_id=?
            ORDER BY a.id DESC
        """;

        List<PatientAppointmentRow> out = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new PatientAppointmentRow(
                            rs.getString("appt_date"),
                            rs.getString("appt_time"),
                            rs.getString("doctor"),
                            rs.getString("status")
                    ));
                }
            }
            return out;

        } catch (Exception e) {
            return out;
        }
    }
}
