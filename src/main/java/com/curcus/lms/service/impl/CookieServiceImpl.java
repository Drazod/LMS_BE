package com.curcus.lms.service.impl;

import com.curcus.lms.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieServiceImpl implements CookieService {
    @Override
    public Optional<Boolean> addCookie(HttpServletResponse response,
                                       String cookieName,
                                       String tokenValue
    ) {
        try {
            Cookie accessTokenCookie = new Cookie(cookieName, tokenValue);
            accessTokenCookie.setHttpOnly(true);
//            accessTokenCookie.setSecure(true); // Set to true in production
            accessTokenCookie.setPath("/");
//            accessTokenCookie.setMaxAge(86400000);
            response.addCookie(accessTokenCookie);
            return Optional.of(true);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.of(false);
        }
    }

    @Override
    public Optional<String> getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return Optional.ofNullable(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        response.addCookie(cookie);
        return Optional.of(true);
    }
}
