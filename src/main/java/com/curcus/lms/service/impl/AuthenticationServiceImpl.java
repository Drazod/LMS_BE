package com.curcus.lms.service.impl;

import com.curcus.lms.exception.*;
import com.curcus.lms.model.entity.*;
import com.curcus.lms.model.mapper.UserMapper;
import com.curcus.lms.model.request.AuthenticationRequest;
import com.curcus.lms.model.request.RegisterRequest;
import com.curcus.lms.model.response.AuthenticationResponse;
import com.curcus.lms.model.response.LoginResponse;
import com.curcus.lms.model.response.ResponseCode;
import com.curcus.lms.model.response.UserResponse;
import com.curcus.lms.repository.RefreshTokenRepository;
import com.curcus.lms.repository.TokenRepository;
import com.curcus.lms.repository.UserRepository;
import com.curcus.lms.service.AuthenticationService;
import com.curcus.lms.service.CookieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTServiceImpl jwtServiceImpl;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CookieService cookieService;

    @Override
    public UserResponse register(RegisterRequest request) {
        try {
            User user = switch (request.getUserRole().toUpperCase()) {
                case "I" -> new Instructor();
                case "S" -> new Student();
                default -> throw new IllegalArgumentException("Invalid user role: " + request.getUserRole());
            };
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setAvtUrl("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.vecteezy.com%2Ffree-vector%2Fuser-icon&psig=AOvVaw2pZJaxsovqmDuBsUPr-0nA&ust=1721966244522000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCJCVs5mmwYcDFQAAAAAdAAAAABAE");


            return userMapper.toUserResponse(repository.save(user));

        } catch (ApplicationException e) {
            throw e;
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void saveUserRefreshToken(User user, String refreshToken) {
        var token = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
    }

    @Override
    public LoginResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Account does not exist"));
//        if (!user.isActivated())
//            throw new InactivatedUserException("Account has not been activated");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new IncorrectPasswordException("Incorrect password");
        }

        var userDetails = UserDetailsImpl.builder()
                .user(user)
                .role(switch(user.getDiscriminatorValue()) {
                            case UserRole.Role.STUDENT -> Role.STUDENT;
                            case UserRole.Role.INSTRUCTOR -> Role.INSTRUCTOR;
                            case UserRole.Role.ADMIN -> Role.ADMIN;
                            default -> throw new ValidationException("Invalid Role");
                        })
                .build();
        var jwtToken = jwtServiceImpl.generateToken(userDetails);
        var refreshToken = jwtServiceImpl.generateRefreshToken(userDetails);
        revokeAllUserTokens(user);
        revokeAllUserRefreshTokens(user);
        saveUserToken(user, jwtToken);
        saveUserRefreshToken(user, refreshToken);

        if (!(cookieService.addCookie(response,
                "accessToken",
                jwtToken).orElse(false)
                && cookieService.addCookie(response,
                "refreshToken",
                refreshToken).orElse(false))) {
            throw new ApplicationException();
        }
        return LoginResponse.builder()
                .userId(user.getUserId())
                .userRole(user.getDiscriminatorValue())
                .name(user.getName())
                .email(user.getEmail())
                .avtUrl(user.getAvtUrl())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .tokens(AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build())
                .userAddress(user.getUserAddress())
                .userCity(user.getUserCity())
                .userCountry(user.getUserCountry())
                .userPostalCode(user.getUserPostalCode())
                .build();
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void revokeAllUserRefreshTokens(User user) {
        var validUserTokens = refreshTokenRepository.findAllValidRefreshTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        refreshTokenRepository.saveAll(validUserTokens);
    }


    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtServiceImpl.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            var userDetails = UserDetailsImpl.builder()
                    .user(user)
                    .role(user.getDiscriminatorValue().equals(UserRole.Role.STUDENT) ? Role.STUDENT : Role.INSTRUCTOR)
                    .build();
            if (jwtServiceImpl.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtServiceImpl.generateToken(userDetails);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}

