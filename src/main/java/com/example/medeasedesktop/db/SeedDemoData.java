package com.example.medeasedesktop.db;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class SeedDemoData {

    private SeedDemoData() {}

    public static void seed() {
        int adminId  = seedUser("ADMIN",   "admin@medease.com",  "admin", "Admin Demo",  1);
        int doc1Id   = seedUser("DOCTOR",  "doctor@medease.com", "22",    "Doctor Demo", 1);
        int doc2Id   = seedUser("DOCTOR",  "cardio@medease.com", "22",    "Dr. Cardio",  1);
        int pat1Uid  = seedUser("PATIENT", "p@gm.com",          "01717", "Patient Demo", 1);
        int pat2Uid  = seedUser("PATIENT", "p2@gm.com",         "01718", "Patient Two",  1);

        int pat1Id = seedPatientRow(pat1Uid, "Patient Demo", "p@gm.com",  "01717");
        int pat2Id = seedPatientRow(pat2Uid, "Patient Two",  "p2@gm.com", "01718");

        seedDoctorProfile(doc1Id, "01800000000", "General Physician", "City Hospital");
        seedDoctorProfile(doc2Id, "01811111111", "Cardiologist",      "Square Hospital");

        seedDoctorSchedule(doc1Id, "Sun", "09:00", "18:00", "WORKING");
        seedDoctorSchedule(doc2Id, "Sun", "10:00", "16:00", "WORKING");

        seedReview(doc1Id, pat1Id, 5, "Very helpful doctor.");
        seedReview(doc2Id, pat2Id, 4, "Good consultation.");

        seedAppointment(pat1Id, doc1Id, "2026-01-12", "10:00", "PENDING",              "Demo");
        seedAppointment(pat1Id, doc1Id, "2026-01-12", "11:15", "APPROVED",             "Demo");
        seedAppointment(pat2Id, doc2Id, "2026-01-11", "15:30", "COMPLETED",            "Demo");
        seedAppointment(pat2Id, doc2Id, "2026-01-13", "12:30", "RESCHEDULE_REQUESTED", "Demo");
    }

    private static int seedUser(String role, String email, String plainPass, String fullName, int enabled) {
        String upsert = """
            INSERT INTO users(role,email,password_hash,full_name,enabled)
            VALUES(?,?,?,?,?)
            ON CONFLICT(email) DO UPDATE SET
                role=excluded.role,
                password_hash=excluded.password_hash,
                full_name=excluded.full_name,
                enabled=excluded.enabled
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(upsert)) {

            String hash = BCrypt.hashpw(plainPass, BCrypt.gensalt(10));

            ps.setString(1, role);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, fullName);
            ps.setInt(5, enabled);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return findUserIdByEmail(email);
    }

    private static int findUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int seedPatientRow(int userId, String fullName, String email, String phone) {
        if (userId <= 0) return 0;

        String upsert = """
            INSERT INTO patients(user_id, full_name, email, phone)
            VALUES(?,?,?,?)
            ON CONFLICT(user_id) DO UPDATE SET
                full_name=excluded.full_name,
                email=excluded.email,
                phone=excluded.phone
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(upsert)) {

            ps.setInt(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String getId = "SELECT id FROM patients WHERE user_id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(getId)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static void seedDoctorProfile(int doctorUserId, String phone, String specialty, String aboutHospital) {
        if (doctorUserId <= 0) return;

        String upsert = """
            INSERT INTO doctor_profile(user_id, phone, specialty, about)
            VALUES(?,?,?,?)
            ON CONFLICT(user_id) DO UPDATE SET
                phone=excluded.phone,
                specialty=excluded.specialty,
                about=excluded.about
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(upsert)) {

            ps.setInt(1, doctorUserId);
            ps.setString(2, phone);
            ps.setString(3, specialty);
            ps.setString(4, aboutHospital);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedDoctorSchedule(int doctorUserId, String day, String start, String end, String type) {
        if (doctorUserId <= 0) return;

        String del = "DELETE FROM doctor_schedule WHERE doctor_id=? AND day=?";
        String ins = """
            INSERT INTO doctor_schedule(doctor_id, day, start_time, end_time, type)
            VALUES(?,?,?,?,?)
        """;

        try (Connection c = DB.getConnection()) {
            try (PreparedStatement d = c.prepareStatement(del)) {
                d.setInt(1, doctorUserId);
                d.setString(2, day);
                d.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(ins)) {
                ps.setInt(1, doctorUserId);
                ps.setString(2, day);
                ps.setString(3, start);
                ps.setString(4, end);
                ps.setString(5, type);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedReview(int doctorUserId, int patientId, int rating, String comment) {
        if (doctorUserId <= 0 || patientId <= 0) return;

        String exists = "SELECT id FROM reviews WHERE doctor_id=? AND patient_id=? AND comment=?";
        String ins = "INSERT INTO reviews(doctor_id, patient_id, rating, comment) VALUES(?,?,?,?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ex = c.prepareStatement(exists)) {

            ex.setInt(1, doctorUserId);
            ex.setInt(2, patientId);
            ex.setString(3, comment);

            try (ResultSet rs = ex.executeQuery()) {
                if (rs.next()) return;
            }

            try (PreparedStatement ps = c.prepareStatement(ins)) {
                ps.setInt(1, doctorUserId);
                ps.setInt(2, patientId);
                ps.setInt(3, rating);
                ps.setString(4, comment);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedAppointment(int patientId, Integer doctorUserIdOrNull,
                                        String date, String time, String status, String notes) {
        if (patientId <= 0) return;

        String exists = """
            SELECT id FROM appointments
            WHERE patient_id=? AND IFNULL(doctor_id,0)=IFNULL(?,0) AND appt_date=? AND appt_time=?
        """;
        String ins = """
            INSERT INTO appointments(patient_id, doctor_id, appt_date, appt_time, status, notes)
            VALUES(?,?,?,?,?,?)
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ex = c.prepareStatement(exists)) {

            ex.setInt(1, patientId);
            if (doctorUserIdOrNull == null) ex.setNull(2, java.sql.Types.INTEGER);
            else ex.setInt(2, doctorUserIdOrNull);
            ex.setString(3, date);
            ex.setString(4, time);

            try (ResultSet rs = ex.executeQuery()) {
                if (rs.next()) return;
            }

            try (PreparedStatement ps = c.prepareStatement(ins)) {
                ps.setInt(1, patientId);
                if (doctorUserIdOrNull == null) ps.setNull(2, java.sql.Types.INTEGER);
                else ps.setInt(2, doctorUserIdOrNull);
                ps.setString(3, date);
                ps.setString(4, time);
                ps.setString(5, status);
                ps.setString(6, notes);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
