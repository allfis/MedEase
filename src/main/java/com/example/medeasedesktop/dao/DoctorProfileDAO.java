package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import com.example.medeasedesktop.model.DoctorProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DoctorProfileDAO {

    public DoctorProfile getByUserId(int userId) {
        String sql = """
            SELECT u.id,
                   u.full_name,
                   u.email,
                   dp.phone,
                   dp.specialty,
                   dp.about,
                   COALESCE((SELECT AVG(r.rating) FROM reviews r WHERE r.doctor_id=u.id),0.0) AS avg_rating,
                   COALESCE((SELECT COUNT(*) FROM reviews r WHERE r.doctor_id=u.id),0) AS total_reviews,
                   COALESCE((SELECT COUNT(*) FROM reviews r 
                             WHERE r.doctor_id=u.id 
                               AND strftime('%Y-%m', r.created_at)=strftime('%Y-%m','now')),0) AS month_reviews
            FROM users u
            LEFT JOIN doctor_profile dp ON dp.user_id=u.id
            WHERE u.id=? AND u.role='DOCTOR'
        """;

        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new DoctorProfile(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("specialty"),
                        rs.getString("about"),
                        rs.getDouble("avg_rating"),
                        rs.getInt("total_reviews"),
                        rs.getInt("month_reviews")
                );
            }
        } catch (Exception e) {
            return null;
        }
    }

    public boolean upsertProfile(DoctorProfile p) {
        String upsert = """
            INSERT INTO doctor_profile(user_id, phone, specialty, about)
            VALUES(?,?,?,?)
            ON CONFLICT(user_id) DO UPDATE SET
                phone=excluded.phone,
                specialty=excluded.specialty,
                about=excluded.about
        """;

        String updateUser = "UPDATE users SET full_name=?, email=? WHERE id=? AND role='DOCTOR'";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(updateUser);
                 PreparedStatement ps2 = c.prepareStatement(upsert)) {

                ps1.setString(1, p.getFullName());
                ps1.setString(2, p.getEmail());
                ps1.setInt(3, p.getUserId());
                ps1.executeUpdate();

                ps2.setInt(1, p.getUserId());
                ps2.setString(2, p.getPhone());
                ps2.setString(3, p.getSpecialty());
                ps2.setString(4, p.getAbout());
                ps2.executeUpdate();

                c.commit();
                return true;
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
