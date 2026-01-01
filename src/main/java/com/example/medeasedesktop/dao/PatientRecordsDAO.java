package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.PatientRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class PatientRecordsDAO {

    public List<PatientRow> listPatients(String idQ, String nameQ, String phoneQ) {
        String base = """
            SELECT id, full_name, phone, gender, dob
            FROM patients
            WHERE 1=1
        """;

        StringBuilder sb = new StringBuilder(base);
        List<Object> params = new ArrayList<>();

        if (idQ != null && !idQ.isBlank()) {
            sb.append(" AND id = ?");
            params.add(parseIntSafe(idQ.trim()));
        }

        if (nameQ != null && !nameQ.isBlank()) {
            sb.append(" AND LOWER(full_name) LIKE ?");
            params.add("%" + nameQ.toLowerCase().trim() + "%");
        }

        if (phoneQ != null && !phoneQ.isBlank()) {
            sb.append(" AND (phone LIKE ?)");
            params.add("%" + phoneQ.trim() + "%");
        }

        sb.append(" ORDER BY id DESC");

        List<PatientRow> out = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String fullName = rs.getString("full_name");
                    String phone = rs.getString("phone");
                    String gender = rs.getString("gender");
                    String dob = rs.getString("dob"); // stored as TEXT

                    int age = calcAgeFromDob(dob);
                    out.add(new PatientRow(id, fullName, phone, gender, age));
                }
            }
        } catch (Exception e) {
            return out;
        }
        return out;
    }

    public boolean addPatient(String fullName, String email, String phone, String dob, String gender, String address) {
        String sql = """
            INSERT INTO patients(full_name, email, phone, dob, gender, address)
            VALUES(?,?,?,?,?,?)
        """;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email == null || email.isBlank() ? null : email.trim());
            ps.setString(3, phone == null || phone.isBlank() ? null : phone.trim());
            ps.setString(4, dob == null || dob.isBlank() ? null : dob.trim());   // "YYYY-MM-DD"
            ps.setString(5, gender == null || gender.isBlank() ? null : gender.trim());
            ps.setString(6, address == null || address.isBlank() ? null : address.trim());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public PatientDetails getPatientDetails(int patientId) {
        String sql = """
            SELECT id, full_name, email, phone, dob, gender, address
            FROM patients
            WHERE id=?
        """;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new PatientDetails(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("address")
                );
            }
        } catch (Exception e) {
            return null;
        }
    }

    public record PatientDetails(
            int id,
            String fullName,
            String email,
            String phone,
            String dob,
            String gender,
            String address
    ) {}

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return -999999; }
    }

    private int calcAgeFromDob(String dob) {
        try {
            if (dob == null || dob.isBlank()) return 0;
            LocalDate d = LocalDate.parse(dob.trim()); // expects YYYY-MM-DD
            return Period.between(d, LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
}
