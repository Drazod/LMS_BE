package com.curcus.lms.model.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "userId")
    private Student student;

    @Column(nullable = false)
    private Date paymentDate;

    @Column(nullable = false)
    private Long totalPrice;
}
