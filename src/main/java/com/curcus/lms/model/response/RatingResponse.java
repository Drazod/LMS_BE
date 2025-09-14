package com.curcus.lms.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse implements Serializable {
    private Long ratingId;
    private Long studentId;
    private Long courseId;
    private Long rating;
    private String comment;
    private LocalDateTime ratingDate;

    @Override
    public String toString() {
        return "StudentResponse{" +
                "ratingId=" + ratingId +
                ", studentId='" + studentId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", rating='" + rating + '\'' +
                ", comment='" + comment + '\'' +
                ", dateTime=" + ratingDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RatingResponse that = (RatingResponse) o;
        return Objects.equals(ratingId, that.ratingId) && Objects.equals(studentId, that.studentId)
                && Objects.equals(courseId, that.courseId) && Objects.equals(rating, that.rating)
                && Objects.equals(comment, that.comment) && Objects.equals(ratingDate, that.ratingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ratingId, studentId, courseId, rating, comment, ratingDate);
    }
}
