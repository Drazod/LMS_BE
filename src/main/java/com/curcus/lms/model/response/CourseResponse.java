package com.curcus.lms.model.response;

import java.io.Serializable;
import java.util.Objects;

import com.curcus.lms.model.entity.Enrollment;
import com.curcus.lms.model.entity.User;
import com.curcus.lms.model.entity.Category;

import lombok.*;
import java.util.Set;

@Getter
@Setter
public class CourseResponse implements Serializable {
    private Long courseId;

    private String courseThumbnail;

    private String title;

    private String description;

    private Long price;

    private Long instructorId;

    private int categoryId;

    @Override
    public String toString() {
        return "CourseResponse{" +
                "courseId=" + courseId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", instructorId=" + instructorId +
                ", categoryId=" + categoryId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CourseResponse that = (CourseResponse) o;
        return Objects.equals(courseId, that.courseId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(price, that.price) &&
                Objects.equals(instructorId, that.instructorId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, price, instructorId, categoryId);
    }
}
