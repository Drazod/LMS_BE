package com.curcus.lms.model.response;

import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.entity.Enrollment;
import com.curcus.lms.model.entity.Section;
import com.curcus.lms.model.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class CourseDetailResponse {
    private String courseThumbnail;

    private String title;

    private String description;

    private Long price;

    private LocalDateTime createdAt;

    private Double avgRating;

    private InstructorResponse instructor;

    private Category category;

    private List<SectionDetailResponse> sections;
}
