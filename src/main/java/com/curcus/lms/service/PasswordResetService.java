package com.curcus.lms.service;

public interface PasswordResetService {
    Boolean requestPasswordReset(String email);
    Boolean resetPassword(String token);
}
