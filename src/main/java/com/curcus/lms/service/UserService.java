package com.curcus.lms.service;

import com.curcus.lms.model.request.RegisterRequest;
import com.curcus.lms.model.response.UserResponse;

public interface UserService {
    UserResponse createUser(RegisterRequest registerRequest);
    UserResponse updateUser(RegisterRequest registerRequest);
    UserResponse deleteUser(String userId);
    UserResponse getUser(String userId);
    UserResponse getUserByEmail(String email);
    UserResponse getUserByUsername(String username);
    UserResponse activateUser(String email);
}
