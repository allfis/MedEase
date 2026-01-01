package com.example.medeasedesktop;

public final class Session {

    private static int userId;
    private static String role;
    private static String email;
    private static String fullName;

    private Session() {}

    public static void set(int id, String r, String e, String name) {
        userId = id;
        role = r;
        email = e;
        fullName = name;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getRole() {
        return role;
    }

    public static String getEmail() {
        return email;
    }

    public static String getFullName() {
        return fullName;
    }
}
