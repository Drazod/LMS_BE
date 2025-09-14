package com.curcus.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.curcus.lms.model.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
