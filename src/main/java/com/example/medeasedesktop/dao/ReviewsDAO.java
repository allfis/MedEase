package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.ReviewRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReviewsDAO {

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
            WHERE role='DOCTOR' AND enabled=1
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

    public List<ReviewRow> getReviews(Integer doctorId, Integer minRating) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            SELECT r.id, r.doctor_id, r.patient_id, r.rating, r.comment, r.created_at,
                   u.full_name AS doctor_name, u.email AS doctor_email,
                   p.full_name AS patient_name
            FROM reviews r
            JOIN users u ON u.id = r.doctor_id
            JOIN patients p ON p.id = r.patient_id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (doctorId != null) {
            sb.append(" AND r.doctor_id=?");
            params.add(doctorId);
        }
        if (minRating != null) {
            sb.append(" AND r.rating>=?");
            params.add(minRating);
        }

        sb.append(" ORDER BY r.created_at DESC");

        List<ReviewRow> out = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String doctorName = rs.getString("doctor_name");
                    String doctorEmail = rs.getString("doctor_email");
                    String d = (doctorName != null && !doctorName.isBlank()) ? doctorName : doctorEmail;

                    out.add(new ReviewRow(
                            rs.getInt("id"),
                            rs.getInt("doctor_id"),
                            rs.getInt("patient_id"),
                            d,
                            rs.getString("patient_name"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getString("created_at")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    public void deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
