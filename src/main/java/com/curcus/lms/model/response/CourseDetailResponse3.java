package com.curcus.lms.model.response;

import java.time.LocalDate;
import java.util.List;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailResponse3 {
    private Long courseId;
    private String courseThumbnail;
    private String title;
    private String description;
    private Long price;
    private Long categoryId;
    private String categoryName;
    private Long instrcutorId;
    private String instructorName;
    private List<StudentResponse> studentList;
    private LocalDate createDate;
    private Double avgRating;
    private String status = "";
}
