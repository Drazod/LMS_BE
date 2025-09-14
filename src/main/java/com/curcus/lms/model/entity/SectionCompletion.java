package com.curcus.lms.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "section_completion")
public class SectionCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sectionCompletionId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "courseId")
    private Course course;
    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "sectionId")
    private Section section;
}
