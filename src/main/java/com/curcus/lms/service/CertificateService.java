package com.curcus.lms.service;

import org.springframework.ui.Model;

public interface CertificateService {
    void updateModel(Model model, Long studentId, Long courseId);
}
