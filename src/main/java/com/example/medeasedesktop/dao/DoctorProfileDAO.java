package com.example.medeasedesktop.dao;

import com.example.medeasedesktop.db.DB;
import java.sql.*;

public class DoctorProfileDAO {

    public static class Profile {
        public String fullName, phone, specialty, about;
    }

    public Profile getProfile(int userId) {
        String sql = """
        SELECT u.full_name, d.phone, d.specialty, d.about
        FROM users u
        LEFT JOIN doctor_profile d ON d.user_id=u.id
        WHERE u.id=?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            Profile p = new Profile();
            p.fullName = rs.getString("full_name");
            p.phone = rs.getString("phone");
            p.specialty = rs.getString("specialty");
            p.about = rs.getString("about");
            return p;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProfile(int userId, String name, String phone, String specialty, String about) {
        try (Connection c = DB.getConnection()) {

            c.prepareStatement("UPDATE users SET full_name=? WHERE id=?")
                    .executeUpdate();

            PreparedStatement ps = c.prepareStatement("""
            INSERT INTO doctor_profile(user_id,phone,specialty,about)
            VALUES(?,?,?,?)
            ON CONFLICT(user_id) DO UPDATE SET
              phone=excluded.phone,
              specialty=excluded.specialty,
              about=excluded.about
            """);

            ps.setInt(1, userId);
            ps.setString(2, phone);
            ps.setString(3, specialty);
            ps.setString(4, about);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
