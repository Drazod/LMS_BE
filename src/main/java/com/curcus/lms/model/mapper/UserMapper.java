package com.curcus.lms.model.mapper;

import com.curcus.lms.model.entity.Admin;
import com.curcus.lms.model.entity.Instructor;
import com.curcus.lms.model.entity.Student;
import com.curcus.lms.model.entity.User;
import com.curcus.lms.model.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

//    Map Student Entity to StudentResponse
    @Mapping(source = "userId", target = "studentId")
    public abstract StudentResponse toResponse(Student student);

    public abstract List<StudentResponse> toResponseList(List<Student> studentList);

    //
    @Mapping(source = "userId", target = "instructorId")
    public abstract InstructorResponse toInstructorResponse(Instructor instructor);

    public abstract List<InstructorResponse> toInstructorResponseList(List<Instructor> instructors);

    //
    @Mapping(source = "userId", target = "adminId")
    public abstract AdminResponse toAdminResponse(Admin admin);

    //
    @Mapping(target = "userRole", expression = "java(getUserRoleById(user))")
    public abstract UserResponse toUserResponse(User user);

    protected String getUserRoleById(User user) {
        return user.getDiscriminatorValue();
    }


    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "userAddress", target = "userAddress")
    @Mapping(source = "userCity", target = "userCity")
    @Mapping(source = "userCountry", target = "userCountry")
    @Mapping(source = "userPostalCode", target = "userPostalCode")
    public abstract UserAddressResponse toUserAddressResponse(Student student);

    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "userAddress", target = "userAddress")
    @Mapping(source = "userCity", target = "userCity")
    @Mapping(source = "userCountry", target = "userCountry")
    @Mapping(source = "userPostalCode", target = "userPostalCode")
    public abstract UserAddressResponse toUserAddressResponse(Instructor instructor);


}
