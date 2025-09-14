package com.curcus.lms.service;

import com.curcus.lms.model.entity.User;

import java.util.Optional;

public interface VerificationTokenService {
    Optional<String> createVerificationToken(String email);
    Optional<User> validateVerificationToken(String token);
    void revokePreviousTokens(Long userId);
}
