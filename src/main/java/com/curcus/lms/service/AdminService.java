package com.curcus.lms.service;

import com.curcus.lms.model.entity.Admin;
import com.curcus.lms.model.response.AdminResponse;
import com.curcus.lms.model.response.CourseStatusResponse;

import java.util.Optional;

public interface AdminService {
    Boolean save(Admin admin);
    Optional<AdminResponse> findById(Long id);
    Optional<AdminResponse> findByEmail(String email);
}
