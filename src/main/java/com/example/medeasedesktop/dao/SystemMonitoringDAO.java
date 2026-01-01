package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.ActivityRow;
import com.example.medeasedesktop.model.SystemStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SystemMonitoringDAO {

    public SystemStats loadStats() {
        int todayAppointments = count("""
            SELECT COUNT(*)
            FROM appointments
            WHERE appt_date = date('now','localtime')
        """);

        int approved = count("""
            SELECT COUNT(*)
            FROM appointments
            WHERE appt_date = date('now','localtime')
              AND status = 'APPROVED'
        """);

        int rejectedCancelled = count("""
            SELECT COUNT(*)
            FROM appointments
            WHERE appt_date = date('now','localtime')
              AND status IN ('REJECTED','CANCELLED')
        """);

        int activeDoctors = count("""
            SELECT COUNT(*)
            FROM users
            WHERE role='DOCTOR' AND enabled=1
        """);

        int onLeaveToday = count("""
            SELECT COUNT(DISTINCT doctor_id)
            FROM doctor_schedule
            WHERE day = date('now','localtime')
              AND type = 'LEAVE'
        """);

        int newReviewsToday = count("""
            SELECT COUNT(*)
            FROM reviews
            WHERE date(created_at,'localtime') = date('now','localtime')
        """);

        return new SystemStats(
                todayAppointments,
                approved,
                rejectedCancelled,
                activeDoctors,
                onLeaveToday,
                newReviewsToday,
                LocalDateTime.now().toString().replace('T', ' ')
        );
    }

    public List<ActivityRow> recentActivity(int limit) {
        String sql = """
            SELECT created_at, event, actor_role, actor_email, details
            FROM activity_log
            ORDER BY id DESC
            LIMIT ?
        """;

        List<ActivityRow> out = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Math.max(1, limit));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String role = nv(rs.getString("actor_role"));
                    String email = nv(rs.getString("actor_email"));
                    String user = (role.isBlank() && email.isBlank()) ? "" : (role + (email.isBlank() ? "" : " • " + email));

                    out.add(new ActivityRow(
                            nv(rs.getString("created_at")),
                            nv(rs.getString("event")),
                            user,
                            nv(rs.getString("details"))
                    ));
                }
            }
        } catch (Exception ignored) {
        }

        return out;
    }

    public void log(String event, String actorRole, String actorEmail, String details) {
        String sql = """
            INSERT INTO activity_log(event, actor_role, actor_email, details)
            VALUES(?,?,?,?)
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nv(event));
            ps.setString(2, nv(actorRole));
            ps.setString(3, nv(actorEmail));
            ps.setString(4, nv(details));
            ps.executeUpdate();

        } catch (Exception ignored) {
        }
    }

    private int count(String sql) {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception ignored) {
        }
        return 0;
    }

    private String nv(String s) {
        return s == null ? "" : s;
    }
}
