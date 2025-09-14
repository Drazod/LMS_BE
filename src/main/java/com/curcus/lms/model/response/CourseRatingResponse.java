package com.curcus.lms.model.response;

import com.curcus.lms.model.dto.RatingDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class CourseRatingResponse {
    private Long totalRating;
    private Double avgRating;
    private RatingDetailDTO ratingDetailDTO;

    public CourseRatingResponse() {
        this.totalRating = 0l;
        this.avgRating = 0d;
        this.ratingDetailDTO = new RatingDetailDTO();
    }

    @Override
    public String toString() {
        return "CourseResponse{" +
                "totalRating=" + totalRating + '\'' +
                ", avgRating='" + avgRating + '\'' +
                ", ratingDetailDTO=" + ratingDetailDTO + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CourseRatingResponse that = (CourseRatingResponse) o;
        return Objects.equals(totalRating, that.totalRating)
                && Objects.equals(avgRating, that.avgRating)
                && Objects.equals(ratingDetailDTO, that.ratingDetailDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                totalRating,
                avgRating,
                ratingDetailDTO
        );
    }
}
