package com.example.medeasedesktop.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SeedDemoData {
    public static void main(String[] args) throws Exception {
        Schema.init();

        try (Connection c = DB.getConnection()) {
            String p1 = "INSERT OR IGNORE INTO patients(id, full_name, email, phone) VALUES(?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(p1)) {
                ps.setInt(1, 1);
                ps.setString(2, "Rahim Uddin");
                ps.setString(3, "rahim@gmail.com");
                ps.setString(4, "01711111111");
                ps.executeUpdate();
            }

            String a1 = "INSERT INTO appointments(patient_id, doctor_id, appt_date, appt_time, status) VALUES(?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(a1)) {
                ps.setInt(1, 1);
                ps.setInt(2, 1);
                ps.setString(3, "2026-01-01");
                ps.setString(4, "10:30");
                ps.setString(5, "PENDING");
                ps.executeUpdate();
            }
        }
    }
}
