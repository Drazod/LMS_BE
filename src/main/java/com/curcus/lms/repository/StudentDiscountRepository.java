package com.curcus.lms.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.curcus.lms.model.entity.StudentDiscount;
import com.curcus.lms.model.entity.StudentDiscountId;
import java.util.List;

@Repository
public interface StudentDiscountRepository extends JpaRepository<StudentDiscount, StudentDiscountId> {
    @Query("select a from StudentDiscount a join a.student b where b.userId = :studentId")
    List<StudentDiscount> getAllDiscountFromStudentId(@Param("studentId") Long studentId);

    @Query("select a from StudentDiscount a join a.student b where isUsed = :isUsed and b.userId = :studentId")
    List<StudentDiscount> getAllDiscountByIsUsed(@Param("isUsed") Boolean isUsed, @Param("studentId") Long studentId);

}
