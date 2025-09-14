package com.curcus.lms.auth;

import com.curcus.lms.model.entity.*;
import com.curcus.lms.repository.RefreshTokenRepository;
import com.curcus.lms.repository.TokenRepository;
import com.curcus.lms.repository.UserRepository;
import com.curcus.lms.service.CookieService;
import com.curcus.lms.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

//        String accessToken = cookieServiceImpl
//                .getCookie(request, "accessToken")
//                .orElse("null");
//        String refreshToken = cookieServiceImpl
//                .getCookie(request, "refreshToken")
//                .orElse("null");
//        String userEmail = "";
//
//        if (accessToken.equals("null") && refreshToken.equals("null")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        final String authHeader = request.getHeader("Authorization");
        String jwt;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
//        userEmail = jwtServiceImpl.extractUsername(accessToken);
        userEmail = jwtService.extractUsername(jwt);
//        RefreshToken refreshToken = new RefreshToken();
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            UserDetailsImpl userDetails = (UserDetailsImpl) this.userDetailsServiceImpl.loadUserByUsername(userEmail);
//            var isAccessTokenValid = tokenRepository.findByToken(accessToken)
//                    .map(t -> !t.isExpired() && !t.isRevoked())
//                    .orElse(false);
//            var isRefreshTokenValid = refreshTokenRepository.findByToken(refreshToken)
//                    .map(t -> !t.isExpired() && !t.isRevoked())
//                    .orElse(false);
//
//            if (jwtServiceImpl.isTokenValid(accessToken, userDetails) && isAccessTokenValid) {
//
//                setWebAuthentication(userDetails, request);
//
//            } else if (jwtServiceImpl.isTokenValid(refreshToken, userDetails)
//                    && isRefreshTokenValid
//                    && !(jwtServiceImpl.isTokenValid(accessToken, userDetails) && isAccessTokenValid)) {
//
//                User user = userDetails.getUser();
//                revokeAllUserTokens(user);
//
//                accessToken = jwtServiceImpl.generateToken(userDetails);
//                saveUserToken(user, accessToken);
//                cookieServiceImpl.addCookie(response, "accessToken", accessToken);
//
//                setWebAuthentication(userDetails, request);
//            } else {
//
//                filterChain.doFilter(request, response);
//
//            }
            UserDetailsImpl userDetails = (UserDetailsImpl) this.userDetailsService.loadUserByUsername(userEmail);
//            System.out.println("----------------------userDetails.toString()------------------------");
            var isAccessTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
//            refreshToken = refreshTokenRepository.findByUser_UserId(userDetails.getUser().getUserId()).orElse(null);
//            var isRefreshTokenValid = !Objects.requireNonNull(refreshToken).isExpired()
//                    && !Objects.requireNonNull(refreshToken).isRevoked();
            if (jwtService.isTokenValid(jwt, userDetails) && isAccessTokenValid) {
//                System.out.println("----------------------DONE 1------------------------");
                setWebAuthentication(userDetails, request);

            }
//            else if (jwtService.isTokenValid(refreshToken.getToken(), userDetails)
////                    && isRefreshTokenValid
////                    && !(jwtService.isTokenValid(jwt, userDetails) && isAccessTokenValid)) {
//////                System.out.println("----------------------DONE 2------------------------");
////                User user = userDetails.getUser();
////                revokeAllUserTokens(user);
////
////                jwt = jwtService.generateToken(userDetails);
////                saveUserToken(user, jwt);
////
////                setWebAuthentication(userDetails, request);
////            }
            else {
//                System.out.println("----------------------FAIL 1------------------------");
                filterChain.doFilter(request, response);

            }
        }
        filterChain.doFilter(request, response);
    }


    private void setWebAuthentication(UserDetailsImpl userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
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
}
