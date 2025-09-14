package com.curcus.lms.service.impl;

import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.model.entity.User;
import com.curcus.lms.model.entity.VerificationToken;
import com.curcus.lms.repository.UserRepository;
import com.curcus.lms.repository.VerificationTokenRepository;
import com.curcus.lms.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VerificationTokenServiceImpl implements VerificationTokenService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Override
    public Optional<String> createVerificationToken(String email) {
        // create Token
        RandomValueStringGenerator stringGenerator = new RandomValueStringGenerator(24);
        String token = stringGenerator.generate();
        try {
            LocalDateTime timeNow = LocalDateTime.now();
            VerificationToken verificationToken = VerificationToken.builder()
                    .token(token)
                    .issueAt(timeNow)
                    .revoked(false)
                    .user(userRepository.findByEmail(email).orElseThrow())
                    .build();
            verificationTokenRepository.save(verificationToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(token);
    }

    @Override
    public Optional<User> validateVerificationToken(String token) {
        try {
            VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                    .orElseThrow(() -> new NotFoundException("Verification token not found"));

            // check if token is revoked
            if (verificationToken.isRevoked()) {
                return Optional.empty();
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiredDate = verificationToken.getIssueAt().plusDays(1);
            // check if token is expired
            if (now.isAfter(expiredDate)) {
                return Optional.empty();
            }

            // revoke token and return user
            verificationToken.setRevoked(true);
            verificationTokenRepository.save(verificationToken);
            User user = userRepository.findById(verificationToken.getUser().getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            return Optional.ofNullable(user);

        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void revokePreviousTokens(Long userId) {
        try {
            List<VerificationToken> list = verificationTokenRepository.findAllByUser_UserId(userId);
            if (!list.isEmpty()) {
                for (VerificationToken verificationToken : list) {
                    verificationToken.setRevoked(true);
                }
                verificationTokenRepository.saveAll(list);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
