package com.curcus.lms.model.mapper;

import com.curcus.lms.model.entity.User;
import com.curcus.lms.model.response.InstructorPublicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.curcus.lms.model.entity.Instructor;
import com.curcus.lms.model.response.InstructorResponse;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InstructorMapper {

    @Mapping(source = "userId", target = "instructorId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    InstructorResponse toResponse(Instructor instructor);


    @Mapping(source = "userId", target = "instructorId")
    @Named("toDetailResponse")
    InstructorResponse toDetailResponse(User instructor);

    @Mapping(source = "avtUrl", target = "avtUrl")
    @Mapping(source = "name", target = "name")
    InstructorPublicResponse toInstructorPublicResponse(Instructor instructor);
}
