package com.curcus.lms.model.entity;

public enum UserRole {
    STUDENT,
    INSTRUCTOR,
    ADMIN;

    public static class Role {
        public static final String STUDENT = "S";
        public static final String INSTRUCTOR = "I";
        public static final String ADMIN = "A";
    }
}
