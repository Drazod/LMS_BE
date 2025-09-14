package com.curcus.lms.model.response;

import java.io.Serializable;

import com.curcus.lms.model.entity.Discount;
import com.curcus.lms.model.entity.Student;
import com.curcus.lms.model.entity.StudentDiscountId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;

@Data
public class StudentDiscountResponse implements Serializable {

    private Discount discount;

    private Long studentId;

    private Boolean isUsed;
}
