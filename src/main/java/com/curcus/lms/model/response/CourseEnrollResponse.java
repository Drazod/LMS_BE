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
public class CourseEnrollResponse implements Serializable {
    private Long courseId;

    private String courseThumbnail;

    private String title;

    private String description;

    private Long price;

    private String instructorName;

    private String categoryName;

    @Override
    public String toString() {
        return "CourseEnrollResponse{" +
                "courseId=" + courseId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", instructorName=" + instructorName +
                ", categoryName=" + categoryName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            CourseEnrollResponse that = (CourseEnrollResponse) o;
        return Objects.equals(courseId, that.courseId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(price, that.price) &&
                Objects.equals(instructorName, that.instructorName) &&
                Objects.equals(categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, price, instructorName, categoryName);
    }
}
