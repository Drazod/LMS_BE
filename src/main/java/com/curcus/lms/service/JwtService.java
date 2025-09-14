package com.curcus.lms.service;

import com.curcus.lms.model.entity.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(UserDetailsImpl userDetails);

    String generateToken(
            Map<String, Object> extraClaims,
            UserDetailsImpl userDetails
    );

    String generateRefreshToken(
            UserDetailsImpl userDetails
    );

    boolean isTokenValid(String token, UserDetailsImpl userDetails);

}
