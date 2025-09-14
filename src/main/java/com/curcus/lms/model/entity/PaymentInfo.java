package com.curcus.lms.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payment_infos")
public class PaymentInfo {
    @Id
    @GeneratedValue
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "userId")
    private User student;

    @OneToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "cartId")
    private Cart cart;

    @Column(nullable = false)
    private String paymentMethod;
    @Column(nullable = false)
    private Long totalPrice;
}
