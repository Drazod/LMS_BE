package com.curcus.lms.service.impl;

import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.Enrollment;
import com.curcus.lms.model.entity.Student;
import com.curcus.lms.repository.CourseRepository;
import com.curcus.lms.repository.EnrollmentRepository;
import com.curcus.lms.repository.StudentRepository;
import com.curcus.lms.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CertificateServiceImpl implements CertificateService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void updateModel(Model model, Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(studentId, courseId);
        if (enrollment == null) {
            throw new NotFoundException("Student has not enrolled this course");
        }
        if (!enrollment.getIsComplete()) {
            throw new NotFoundException("Student has not completed this course");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        model.addAttribute("studentName", student.getName());
        model.addAttribute("courseName", course.getTitle());

        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = formatter.format(enrollment.getCompletionDate());
        model.addAttribute("completionDate", formattedDate);
    }
}
