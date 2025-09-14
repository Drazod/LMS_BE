package com.curcus.lms.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.Set;
import java.util.UUID;

import com.curcus.lms.util.CodeDicsountUtil;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue
    private Long discountId;

    @Column(unique = true, nullable = false, updatable = false)
    private String code;

    @PrePersist
    protected void onCreate() {
        if (this.code == null) {
            this.code = CodeDicsountUtil.generateDiscountCode();
        }
    }

    @Column
    private String description;

    @Column(name = "discount_value", nullable = false)
    private Long value;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

}
