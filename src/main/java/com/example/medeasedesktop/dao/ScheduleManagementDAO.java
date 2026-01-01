package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.ScheduleRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ScheduleManagementDAO {

    public static class DoctorItem {
        public final int id;
        public final String label;

        public DoctorItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public List<DoctorItem> getDoctors() {
        String sql = """
            SELECT id, full_name, email
            FROM users
            WHERE role = 'DOCTOR' AND enabled = 1
            ORDER BY full_name
        """;

        List<DoctorItem> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("full_name");
                String email = rs.getString("email");
                String label = (name != null && !name.isBlank()) ? name : email;
                list.add(new DoctorItem(id, label));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<ScheduleRow> getAllSchedules() {
        String sql = """
            SELECT ds.id, ds.doctor_id, ds.day, ds.start_time, ds.end_time,
                   ds.type, ds.created_at,
                   u.full_name, u.email
            FROM doctor_schedule ds
            JOIN users u ON u.id = ds.doctor_id
            ORDER BY u.full_name, ds.day, ds.start_time
        """;

        List<ScheduleRow> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String doctorName = (fullName != null && !fullName.isBlank())
                        ? fullName
                        : email;

                list.add(new ScheduleRow(
                        rs.getInt("id"),
                        rs.getInt("doctor_id"),
                        doctorName,
                        rs.getString("day"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("type"),
                        rs.getString("created_at")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public void insertSchedule(int doctorId, String day,
                               String startTime, String endTime, String type) {

        String sql = """
            INSERT INTO doctor_schedule (doctor_id, day, start_time, end_time, type)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setString(2, day);
            ps.setString(3, startTime);
            ps.setString(4, endTime);
            ps.setString(5, type);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSchedule(int id, String day,
                               String startTime, String endTime, String type) {

        String sql = """
            UPDATE doctor_schedule
            SET day = ?, start_time = ?, end_time = ?, type = ?
            WHERE id = ?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, day);
            ps.setString(2, startTime);
            ps.setString(3, endTime);
            ps.setString(4, type);
            ps.setInt(5, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSchedule(int id) {
        String sql = "DELETE FROM doctor_schedule WHERE id = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
