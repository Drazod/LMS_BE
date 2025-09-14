package com.curcus.lms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class RatingDetailDTO {
    private Long oneStar;
    private Long twoStar;
    private Long threeStar;
    private Long fourStar;
    private Long fiveStar;

    public RatingDetailDTO() {
        this.oneStar = 0L;
        this.twoStar = 0L;
        this.threeStar = 0L;
        this.fourStar = 0L;
        this.fiveStar = 0L;
    }
}
