package com.curcus.lms.service.impl;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.entity.Admin;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.CourseStatus;
import com.curcus.lms.model.mapper.UserMapper;
import com.curcus.lms.model.response.AdminResponse;
import com.curcus.lms.model.response.CourseStatusResponse;
import com.curcus.lms.repository.AdminRepository;
import com.curcus.lms.repository.CourseRepository;
import com.curcus.lms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Boolean save(Admin admin) {
        try {
            adminRepository.save(admin);
            return true;
        } catch (ApplicationException a) {
            return false;
        }
    }

    @Override
    public Optional<AdminResponse> findById(Long id) {
        try {
            return adminRepository.findById(id).map(userMapper::toAdminResponse);
        } catch (ApplicationException a) {
            throw a;
        }
    }

    @Override
    public Optional<AdminResponse> findByEmail(String email) {
        try {
            return adminRepository.findByEmail(email).map(userMapper::toAdminResponse);
        } catch (ApplicationException a) {
            throw a;
        }
    }

}
