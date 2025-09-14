package com.curcus.lms.service;



import com.curcus.lms.model.request.RegisterRequest;
import com.curcus.lms.model.request.AuthenticationRequest;
import com.curcus.lms.model.response.AuthenticationResponse;
import com.curcus.lms.model.response.LoginResponse;
import com.curcus.lms.model.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    UserResponse register(RegisterRequest request);

    LoginResponse authenticate(AuthenticationRequest request, HttpServletResponse response);

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;
}
