package com.example.medeasedesktop.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DB {
    private static final String URL;

    static {
        try {
            Path dbPath = Paths.get(System.getProperty("user.home"), "medease.db");
            Files.createDirectories(dbPath.getParent());
            URL = "jdbc:sqlite:" + dbPath.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DB() {}

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA journal_mode = WAL");
        }
        return c;
    }
}
