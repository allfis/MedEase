package com.example.medeasedesktop.db;

import java.sql.Connection;
import java.sql.Statement;

public final class Schema {
    private Schema() {}

    public static void init() {

        String users = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            role TEXT NOT NULL CHECK(role IN ('ADMIN','DOCTOR')),
            email TEXT UNIQUE NOT NULL,
            password_hash TEXT NOT NULL,
            full_name TEXT,
            enabled INTEGER NOT NULL DEFAULT 1
        );
        """;

        String doctorProfile = """
        CREATE TABLE IF NOT EXISTS doctor_profile (
            user_id INTEGER PRIMARY KEY,
            phone TEXT,
            specialty TEXT,
            about TEXT,
            FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
        );
        """

                ;

        String patients = """
        CREATE TABLE IF NOT EXISTS patients (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            full_name TEXT NOT NULL,
            email TEXT,
            phone TEXT,
            dob TEXT,
            gender TEXT,
            address TEXT
        );
        """;

        String appointments = """
        CREATE TABLE IF NOT EXISTS appointments (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            patient_id INTEGER NOT NULL,
            doctor_id INTEGER,
            appt_date TEXT NOT NULL,
            appt_time TEXT NOT NULL,
            status TEXT NOT NULL DEFAULT 'PENDING'
                CHECK(status IN ('PENDING','APPROVED','REJECTED','CANCELLED','COMPLETED','RESCHEDULE_REQUESTED')),
            notes TEXT,
            created_at TEXT NOT NULL DEFAULT (datetime('now')),
            updated_at TEXT NOT NULL DEFAULT (datetime('now')),
            FOREIGN KEY(patient_id) REFERENCES patients(id) ON DELETE CASCADE,
            FOREIGN KEY(doctor_id) REFERENCES users(id) ON DELETE SET NULL
        );
        """;

        String schedule = """
        CREATE TABLE IF NOT EXISTS doctor_schedule (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            doctor_id INTEGER NOT NULL,
            day TEXT NOT NULL,
            start_time TEXT NOT NULL,
            end_time TEXT NOT NULL,
            type TEXT NOT NULL DEFAULT 'WORKING'
                CHECK(type IN ('WORKING','LEAVE','HOLIDAY')),
            created_at TEXT NOT NULL DEFAULT (datetime('now')),
            FOREIGN KEY(doctor_id) REFERENCES users(id) ON DELETE CASCADE
        );
        """;

        String prescriptions = """
        CREATE TABLE IF NOT EXISTS prescriptions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            doctor_id INTEGER NOT NULL,
            patient_id INTEGER NOT NULL,
            created_at TEXT NOT NULL DEFAULT (datetime('now')),
            status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK(status IN ('ACTIVE','UPDATED','ARCHIVED')),
            details TEXT NOT NULL,
            FOREIGN KEY(doctor_id) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY(patient_id) REFERENCES patients(id) ON DELETE CASCADE
        );
        """;

        String reviews = """
        CREATE TABLE IF NOT EXISTS reviews (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            doctor_id INTEGER NOT NULL,
            patient_id INTEGER NOT NULL,
            rating INTEGER NOT NULL CHECK(rating BETWEEN 1 AND 5),
            comment TEXT,
            created_at TEXT NOT NULL DEFAULT (datetime('now')),
            FOREIGN KEY(doctor_id) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY(patient_id) REFERENCES patients(id) ON DELETE CASCADE
        );
        """;

        String activityLog = """
        CREATE TABLE IF NOT EXISTS activity_log (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            created_at TEXT NOT NULL DEFAULT (datetime('now')),
            event TEXT NOT NULL,
            actor_role TEXT,
            actor_email TEXT,
            details TEXT
        );
        """;

        try (Connection c = DB.getConnection(); Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
            s.execute("PRAGMA journal_mode = WAL");

            s.execute(users);
            s.execute(doctorProfile);
            s.execute(patients);
            s.execute(appointments);
            s.execute(schedule);
            s.execute(prescriptions);
            s.execute(reviews);
            s.execute(activityLog);

            try { s.execute("ALTER TABLE users ADD COLUMN enabled INTEGER NOT NULL DEFAULT 1"); } catch (Exception ignored) {}
            try { s.execute("ALTER TABLE appointments ADD COLUMN updated_at TEXT NOT NULL DEFAULT (datetime('now'))"); } catch (Exception ignored) {}

            try {
                s.execute("""
INSERT OR IGNORE INTO users(role,email,password_hash,full_name,enabled)
VALUES('ADMIN','admin@medease.com','11','Admin',1)
""");

                s.execute("""
INSERT OR IGNORE INTO users(role,email,password_hash,full_name,enabled)
VALUES('DOCTOR','doctor@medease.com','22','Doctor',1)
""");

            } catch (Exception ignored) {}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
