package com.curcus.lms.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.curcus.lms.model.entity.OrderItems;
import com.curcus.lms.model.entity.OrderItemsId;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, OrderItemsId> {
    @Query(nativeQuery = true, value = "select count(distinct o.student_id) from orders o join order_items oi on o.order_id  = oi.order_id join courses c on oi.course_id = c.course_id where c.instructor_id = :instructorId")
    Long getTotalUsersBuyedCourses(Long instructorId);

    @Query(nativeQuery = true, value = "select sum(oi.price) from order_items oi join orders o on oi.order_id = o.order_id join courses c on oi.course_id = c.course_id where c.instructor_id = :instructorId and o.payment_date >= :start and o.payment_date <= :end")
    Long getRevenueStatisticsForYears(Long instructorId, LocalDate start, LocalDate end);
}
