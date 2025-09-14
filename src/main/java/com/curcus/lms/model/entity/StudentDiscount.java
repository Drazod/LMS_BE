package com.curcus.lms.model.entity;

import jakarta.persistence.*;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "student_discounts")
public class StudentDiscount {
    @EmbeddedId
    private StudentDiscountId Id;

    @ManyToOne
    @MapsId("discountId")
    @JoinColumn(name = "discount_id", referencedColumnName = "discountId")
    private Discount discount;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id", referencedColumnName = "userId")
    private Student student;

    @Column(columnDefinition = "boolean default false")
    private Boolean isUsed;

}
