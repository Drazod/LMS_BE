package com.curcus.lms.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
@Data
public class DiscountRequest {

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Value is mandatory")
    @Min(value = 0, message = "value must be a positive number")
    private Long value;

    @NotNull(message = "StartDate ID is mandatory")
    private Date startDate;
    @NotNull(message = "EndDate ID is mandatory")
    private Date endDate;

    public DiscountRequest(Long value, String description, Date startDate, Date endDate) {
        super();
        this.value = value;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
