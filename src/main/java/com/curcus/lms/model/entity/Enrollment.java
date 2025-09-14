package com.curcus.lms.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicInsert
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long enrollmentId;
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "userId")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "courseId")
    private Course course;
    @Column(nullable = false)
    private Date enrollmentDate;
    @ColumnDefault("false")
    private Boolean isComplete;
    @ColumnDefault("1")
    private Long currentSectionPosition;

    private Date completionDate;
}
