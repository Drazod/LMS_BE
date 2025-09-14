package com.curcus.lms.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.StudentDiscount;
import com.curcus.lms.model.entity.StudentDiscountId;
import com.curcus.lms.model.entity.Discount;
import com.curcus.lms.model.entity.Student;
import com.curcus.lms.model.mapper.DiscountMapper;
import com.curcus.lms.model.request.DiscountRequest;
import com.curcus.lms.model.response.DiscountResponse;
import com.curcus.lms.model.response.StudentDiscountResponse;
import com.curcus.lms.repository.CourseRepository;
import com.curcus.lms.repository.DiscountRepository;
import com.curcus.lms.repository.StudentDiscountRepository;
import com.curcus.lms.repository.StudentRepository;
import com.curcus.lms.service.DiscountService;
import java.util.Collections;

@Service
public class DiscountServiceImpl implements DiscountService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DiscountMapper discountMapper;
    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentDiscountRepository studentDiscountRepository;

    @Override
    public List<DiscountResponse> findAll() {
        List<Discount> discounts = discountRepository.findAll();
        List<DiscountResponse> discountResponses = discountMapper.toListResponse(discounts);
        if (discountResponses.isEmpty()) {
            throw new NotFoundException("No discount exists");
        }
        return discountResponses;
    }

    @Override
    public DiscountResponse findDiscountById(Long discountId) {
        Discount discount = discountRepository.findById(discountId).orElse(null);
        if (discount == null) {
            throw new NotFoundException("Discount has not existed with discountId " +
                    discountId);
        }
        DiscountResponse discountResponse = discountMapper.toResponse(discount);
        return discountResponse;
    }

    @Override
    public DiscountResponse createDiscount(DiscountRequest discountRequest)

    {
        Discount discount = discountMapper.toEntity(discountRequest);
        Discount savedDiscount = discountRepository.save(discount);
        return discountMapper.toResponse(savedDiscount);
    }

    @Override
    public DiscountResponse updateDiscount(DiscountRequest discountRequest, Long discount_id) {
        try {
            Discount discount = discountMapper.toEntity(discountRequest);
            discount.setDiscountId(discount_id);
            // Save update discount
            discountRepository.save(discount);
            // Mapping course to courseResponse
            DiscountResponse discountResponse = discountMapper.toResponse(discount);
            return discountResponse;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }

    @Override
    public DiscountResponse deleteDisCount(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Discount has not existed with id " + id));
        // if (!discount.getEnrollment().isEmpty())
        // throw new ValidationException("The course cannot be deleted because
        // someone
        // is currently enrolled");
        discountRepository.deleteById(id);
        return discountMapper.toResponse(discount);
    }

    @Override
    public StudentDiscountResponse addDiscountToStudent(Long discountId, Long studentId) {
        try {
            Discount discount = discountRepository.findById(discountId).orElse(null);
            if (discount == null) {
                throw new NotFoundException("Discount has not existed with id " +
                        discountId);
            }
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                throw new NotFoundException("Student has not existed with id " + studentId);
            }
            StudentDiscountId studentDiscountId = new StudentDiscountId(studentId, discountId);
            StudentDiscount studentDiscount = studentDiscountRepository.findById(studentDiscountId).orElse(null);
            if (studentDiscount != null) {
                throw new NotFoundException("Student already have discount");
            }
            studentDiscount = new StudentDiscount();
            studentDiscount.setId(studentDiscountId);
            studentDiscount.setStudent(student);
            studentDiscount.setDiscount(discount);
            studentDiscount.setIsUsed(false);
            studentDiscountRepository.save(studentDiscount);

            return discountMapper.toResponse(studentDiscount);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }

    @Override
    public List<StudentDiscountResponse> findAllDiscountFromStudent(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            throw new NotFoundException("Student has not existed with id " + studentId);
        }
        List<StudentDiscount> studentDiscounts = studentDiscountRepository.getAllDiscountFromStudentId(studentId);

        if (studentDiscounts == null || studentDiscounts.isEmpty()) {
            throw new NotFoundException("No discounts found for student with ID: " + studentId);
        }

        List<StudentDiscountResponse> studentDiscountResponses = discountMapper
                .toListResponseDiscountFromStudent(studentDiscounts);
        return studentDiscountResponses;
    }

    @Override
    public List<StudentDiscountResponse> findAllDiscountFromDiscountId(Long discountId, Long studentId) {
        Discount discount = discountRepository.findById(discountId).orElse(null);
        if (discount == null) {
            throw new NotFoundException("Discount has not existed with id " + discountId);
        }
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            throw new NotFoundException("Student has not existed with id " + studentId);
        }
        StudentDiscountId studentDiscountId = new StudentDiscountId(studentId, discountId);
        Iterable<StudentDiscountId> ids = Collections.singletonList(studentDiscountId);

        List<StudentDiscount> studentDiscounts = studentDiscountRepository.findAllById(ids);

        if (studentDiscounts == null || studentDiscounts.isEmpty()) {
            throw new NotFoundException("No discounts found for student with ID: " + discountId);
        }

        List<StudentDiscountResponse> studentDiscountResponses = discountMapper
                .toListResponseDiscountFromStudent(studentDiscounts);
        return studentDiscountResponses;
    }

    @Override
    public List<StudentDiscountResponse> findAllDiscountByIsUsed(Boolean isUsed, Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            throw new NotFoundException("Student has not existed with id " + studentId);
        }
        List<StudentDiscount> studentDiscounts = studentDiscountRepository.getAllDiscountByIsUsed(isUsed, studentId);
        if (studentDiscounts == null || studentDiscounts.isEmpty()) {
            throw new NotFoundException("No discounts found for student ");
        }

        List<StudentDiscountResponse> studentDiscountResponses = discountMapper
                .toListResponseDiscountFromStudent(studentDiscounts);
        return studentDiscountResponses;
    }

    @Override
    public void deleteDiscountFromStudent(Long discountId, Long studentId) {
        Discount discount = discountRepository.findById(discountId).orElse(null);
        if (discount == null) {
            throw new NotFoundException("Discount has not existed with id " +
                    discountId);
        }
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            throw new NotFoundException("Student has not existed with id " + studentId);
        }
        StudentDiscountId studentDiscountId = new StudentDiscountId(studentId, discountId);
        StudentDiscount studentDiscount = studentDiscountRepository.findById(studentDiscountId).orElse(null);
        if (studentDiscount == null) {
            throw new NotFoundException("DiscountId " + discountId + "has not exist in studentId " + studentId);
        }
        studentDiscount.setIsUsed(true);
        studentDiscountRepository.save(studentDiscount);
    }

    @Override
    public Long findDiscountByCode(String discountCode, Long studentId) {
        try {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                throw new NotFoundException("Student has not existed with id " + studentId);
            }
            Discount discount = discountRepository.findDiscountByCode(discountCode);
            if (discount == null) {
                throw new NotFoundException("Discount has not existed with discountCode " + discountCode);
            }
            StudentDiscountId studentDiscountId = new StudentDiscountId(studentId, discount.getDiscountId());
            StudentDiscount studentDiscount = studentDiscountRepository.findById(studentDiscountId).orElse(null);
            if (studentDiscount == null) {
                throw new NotFoundException("Discount has not existed with studentId " + studentId);
            }
            return discount.getDiscountId();
        } catch (NotFoundException ex) {
            throw ex;
        }
    }
}
