package com.curcus.lms.model.response;

import java.sql.Date;

import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

import com.curcus.lms.constants.ContentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DiscountResponse {
    private Long discountId;

    private String description;

    private String code;

    private Long value;

    private Date startDate;

    private Date endDate;
}
