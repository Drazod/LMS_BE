package com.curcus.lms.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    Boolean sendEmailToStudent(String to);
    Boolean sendEmailToInstructor(String to);
    Boolean sendPasswordResetConfirmation(String to, String token);
    Boolean sendPassword(String to, String password);
    Boolean sendHtmlEmailWithButton(String to, String subject, String body1, String body2, String link);
    Boolean sendHtmlEmailWithoutButton(String to, String subject, String body1, String body2);
}
