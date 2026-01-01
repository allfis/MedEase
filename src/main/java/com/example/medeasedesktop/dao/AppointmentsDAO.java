package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.AppointmentRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsDAO {

    public record DoctorOption(int id, String name, String email) {
        @Override
        public String toString() {
            String n = name == null || name.isBlank() ? "Doctor" : name;
            return n + " (" + email + ")";
        }
    }

    public List<AppointmentRow> listForAdmin() {
        String sql = """
            SELECT a.id,
                   p.full_name AS patient,
                   a.appt_date AS appt_date,
                   a.appt_time AS appt_time,
                   COALESCE(u.full_name,'Unassigned') AS doctor,
                   a.status AS status
            FROM appointments a
            JOIN patients p ON p.id=a.patient_id
            LEFT JOIN users u ON u.id=a.doctor_id
            ORDER BY a.id DESC
        """;
        return queryAppointments(sql, null);
    }

    public List<AppointmentRow> listForDoctor(int doctorId) {
        String sql = """
            SELECT a.id,
                   p.full_name AS patient,
                   a.appt_date AS appt_date,
                   a.appt_time AS appt_time,
                   COALESCE(u.full_name,'Unassigned') AS doctor,
                   a.status AS status
            FROM appointments a
            JOIN patients p ON p.id=a.patient_id
            LEFT JOIN users u ON u.id=a.doctor_id
            WHERE a.doctor_id=?
            ORDER BY a.id DESC
        """;
        return queryAppointments(sql, ps -> ps.setInt(1, doctorId));
    }

    private interface Binder {
        void bind(PreparedStatement ps) throws Exception;
    }

    private List<AppointmentRow> queryAppointments(String sql, Binder binder) {
        List<AppointmentRow> list = new ArrayList<>();
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AppointmentRow(
                            rs.getInt("id"),
                            rs.getString("patient"),
                            rs.getString("appt_date"),
                            rs.getString("appt_time"),
                            rs.getString("doctor"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public List<DoctorOption> listEnabledDoctors() {
        List<DoctorOption> docs = new ArrayList<>();
        String sql = "SELECT id, full_name, email FROM users WHERE role='DOCTOR' AND enabled=1 ORDER BY full_name";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                docs.add(new DoctorOption(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email")
                ));
            }
        } catch (Exception e) {
            return docs;
        }
        return docs;
    }

    public boolean approve(int appointmentId, String actorRole, String actorEmail) {
        return updateStatus(appointmentId, "APPROVED", actorRole, actorEmail, "Approved appointment #" + appointmentId);
    }

    public boolean reject(int appointmentId, String actorRole, String actorEmail) {
        return updateStatus(appointmentId, "REJECTED", actorRole, actorEmail, "Rejected appointment #" + appointmentId);
    }

    public boolean markCompleted(int appointmentId, String actorRole, String actorEmail) {
        return updateStatus(appointmentId, "COMPLETED", actorRole, actorEmail, "Completed appointment #" + appointmentId);
    }

    public boolean requestReschedule(int appointmentId, String actorRole, String actorEmail) {
        return updateStatus(appointmentId, "RESCHEDULE_REQUESTED", actorRole, actorEmail, "Reschedule requested for appointment #" + appointmentId);
    }

    public boolean assignDoctor(int appointmentId, int doctorId, String actorRole, String actorEmail) {
        String sql = "UPDATE appointments SET doctor_id=?, updated_at=datetime('now') WHERE id=?";
        String log = "INSERT INTO activity_log(event, actor_role, actor_email, details) VALUES(?,?,?,?)";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(sql);
                 PreparedStatement lg = c.prepareStatement(log)) {

                ps.setInt(1, doctorId);
                ps.setInt(2, appointmentId);
                int a = ps.executeUpdate();

                lg.setString(1, "APPOINTMENT_ASSIGNED");
                lg.setString(2, actorRole);
                lg.setString(3, actorEmail);
                lg.setString(4, "Assigned doctor_id=" + doctorId + " to appointment #" + appointmentId);
                lg.executeUpdate();

                c.commit();
                return a == 1;
            } catch (Exception ex) {
                c.rollback();
                return false;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateStatus(int appointmentId, String status, String actorRole, String actorEmail, String details) {
        String sql = "UPDATE appointments SET status=?, updated_at=datetime('now') WHERE id=?";
        String log = "INSERT INTO activity_log(event, actor_role, actor_email, details) VALUES(?,?,?,?)";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(sql);
                 PreparedStatement lg = c.prepareStatement(log)) {

                ps.setString(1, status);
                ps.setInt(2, appointmentId);
                int a = ps.executeUpdate();

                lg.setString(1, "APPOINTMENT_STATUS_CHANGED");
                lg.setString(2, actorRole);
                lg.setString(3, actorEmail);
                lg.setString(4, details + " (status=" + status + ")");
                lg.executeUpdate();

                c.commit();
                return a == 1;
            } catch (Exception ex) {
                c.rollback();
                return false;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            return false;
        }
    }
}
