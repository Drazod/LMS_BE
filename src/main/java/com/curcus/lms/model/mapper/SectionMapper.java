package com.curcus.lms.model.mapper;

import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.Section;
import com.curcus.lms.model.entity.Content;

import com.curcus.lms.model.response.CourseDetailResponse;
import com.curcus.lms.model.response.ContentDetailResponse;
import com.curcus.lms.model.response.SectionCreateResponse;
import com.curcus.lms.model.response.SectionDetailResponse;

import com.curcus.lms.model.response.SectionDetailResponse2;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class SectionMapper {
	@Mapping(source = "course.courseId", target = "courseId")
    public abstract SectionCreateResponse toResponse(Section section);

    @Mapping(source = "section.sectionId", target = "sectionId")
    @Mapping(source = "sectionName", target = "sectionName")
    @Mapping(source = "position", target = "position")
    public abstract SectionDetailResponse toDetailResponse(Section section);

    @Mapping(source = "section.sectionId", target = "sectionId")
    @Mapping(source = "sectionName", target = "sectionName")
    @Mapping(source = "position", target = "position")
    @Mapping(target = "contents", expression = "java(mapSortedContents(section))")
    public abstract SectionDetailResponse2 toDetailResponse2(Section section);
    protected List<ContentDetailResponse> mapSortedContents(Section section) {
        
        return section.getContents().stream()
                .sorted(Comparator.comparing(Content::getPosition))
                .map(this::mapContent)
                .collect(Collectors.toList());
    }

    protected abstract ContentDetailResponse mapContent(Content content);
}