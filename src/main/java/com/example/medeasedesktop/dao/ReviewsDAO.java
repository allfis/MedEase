package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.ReviewRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReviewsDAO {

    public List<ReviewRow> list(String q, Integer ratingFilter) {
        List<ReviewRow> list = new ArrayList<>();

        String where = " WHERE 1=1 ";
        String like = null;

        if (q != null && !q.trim().isEmpty()) {
            where += " AND (lower(p.full_name) LIKE ? OR lower(COALESCE(u.full_name,'')) LIKE ? OR lower(COALESCE(r.comment,'')) LIKE ?) ";
            like = "%" + q.trim().toLowerCase() + "%";
        }

        if (ratingFilter != null) {
            where += " AND r.rating=? ";
        }

        String sql = """
            SELECT r.id AS id,
                   p.full_name AS patient,
                   COALESCE(u.full_name,'Doctor') AS doctor,
                   r.rating AS rating,
                   COALESCE(r.comment,'') AS comment,
                   strftime('%Y-%m-%d', r.created_at) AS created_at
            FROM reviews r
            JOIN patients p ON p.id=r.patient_id
            JOIN users u ON u.id=r.doctor_id
        """ + where + " ORDER BY r.id DESC";

        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int idx = 1;

            if (like != null) {
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (ratingFilter != null) {
                ps.setInt(idx++, ratingFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ReviewRow(
                            rs.getInt("id"),
                            rs.getString("patient"),
                            rs.getString("doctor"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getString("created_at")
                    ));
                }
            }
        } catch (Exception e) {
            return list;
        }

        return list;
    }

    public boolean deleteReview(int reviewId, String actorRole, String actorEmail) {
        String del = "DELETE FROM reviews WHERE id=?";
        String log = "INSERT INTO activity_log(event, actor_role, actor_email, details) VALUES(?,?,?,?)";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(del);
                 PreparedStatement lg = c.prepareStatement(log)) {

                ps.setInt(1, reviewId);
                int a = ps.executeUpdate();

                lg.setString(1, "REVIEW_DELETED");
                lg.setString(2, actorRole);
                lg.setString(3, actorEmail);
                lg.setString(4, "Deleted review #" + reviewId);
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
