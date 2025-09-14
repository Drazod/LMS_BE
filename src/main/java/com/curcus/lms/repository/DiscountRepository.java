package com.curcus.lms.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.curcus.lms.model.entity.Cart;
import com.curcus.lms.model.entity.Discount;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("SELECT a FROM Discount a WHERE a.code.userId = :discountCode")
    Discount findDiscountByCode(@Param("discountCode") String discountCode);
}
