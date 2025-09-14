package com.curcus.lms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.curcus.lms.model.entity.Instructor;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    List<Instructor> findByName(String name);

    Instructor findByEmail(String email);

    Instructor findByPhoneNumber(String phoneNumber);

    Instructor findByPassword(String password);

    @Query("SELECT SUM(c.price) FROM OrderItems c WHERE c.course.instructor.userId = ?1")
    Long getTotalRevenue(Long instructorId);
}
