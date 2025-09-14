package com.curcus.lms.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_id", "course_id" })
})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "userId")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "courseId")
    private Course course;
    @Column(nullable = false)
    private Long rating;
    @Column(nullable = true)
    private String comment;
    @Column
    private LocalDateTime ratingDate;
}
