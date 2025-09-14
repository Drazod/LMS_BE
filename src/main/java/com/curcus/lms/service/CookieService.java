package com.curcus.lms.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface CookieService {
    Optional<Boolean> addCookie(HttpServletResponse response, String cookieName, String tokenValue);
    Optional<String> getCookie(HttpServletRequest request, String cookieName);
    Optional<Boolean> removeCookie(HttpServletResponse response, String cookieName);
}
