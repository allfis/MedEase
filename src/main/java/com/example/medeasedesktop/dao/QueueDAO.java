package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.QueueViewRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QueueDAO {

    public List<QueueViewRow> getTodayQueue(int doctorId) {

        String sql = """
            SELECT a.appt_time, p.full_name, a.status
            FROM appointments a
            JOIN patients p ON p.id = a.patient_id
            WHERE a.doctor_id = ? AND a.appt_date = date('now')
            ORDER BY a.appt_time
        """;

        List<QueueViewRow> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new QueueViewRow(
                            rs.getString("appt_time"),
                            rs.getString("full_name"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
