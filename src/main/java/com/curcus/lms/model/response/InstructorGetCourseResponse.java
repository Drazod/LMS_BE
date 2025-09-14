package com.curcus.lms.model.response;

import com.curcus.lms.model.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class InstructorGetCourseResponse implements Serializable {
    private Long courseId;
    private String courseThumbnail;
    private String title;
    private String description;
    private Long price;
    private Long categoryId;
    private List<Student> studentList;
    private LocalDateTime LocalDate;
    private String status;

    @Override
    public String toString() {
        return "InstructorGetCourseResponse{" +
                "courseId=" + courseId +
                ", courseThumbnail='" + courseThumbnail + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", studentList=" + studentList +
                ", LocalDate=" + LocalDate +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstructorGetCourseResponse that = (InstructorGetCourseResponse) o;
        return Objects.equals(courseId, that.courseId) && Objects.equals(courseThumbnail, that.courseThumbnail) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(price, that.price) && Objects.equals(categoryId, that.categoryId) && Objects.equals(studentList, that.studentList) && Objects.equals(LocalDate, that.LocalDate) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, courseThumbnail, title, description, price, categoryId, studentList, LocalDate, status);
    }
}
