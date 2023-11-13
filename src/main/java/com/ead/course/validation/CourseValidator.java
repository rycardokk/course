package com.ead.course.validation;



import com.ead.course.configs.security.AuthenticationCurrentUserService;
import com.ead.course.dtos.CourseDto;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


import java.util.Optional;
import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    @Qualifier("defaultValidator")
    private final Validator validator;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    final
    UserService userService;

    final
    AuthenticationCurrentUserService authenticationCurrentUserService;

    public CourseValidator(@Qualifier("defaultValidator") Validator validator, UserService userService,
                           AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.validator = validator;
        this.userService = userService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDto courseDto = (CourseDto) o;
        validator.validate(courseDto, errors);
        if(!errors.hasErrors()){
            validateUserInstructor(courseDto.getUserInstructor(), errors);
        }
    }

    private void validateUserInstructor(UUID userInstructor, Errors errors) {
        var currentUser = authenticationCurrentUserService.getCurrentUser();
        var userModelOptional = userService.findById(userInstructor);

        if (!currentUser.getUserId().equals(userInstructor)) {
            throw new AccessDeniedException("Forbidden");
        }

        if (userModelOptional.isEmpty()) {
            errors.rejectValue("userInstructor", "UserIstructorError", "Instructor not found.");
            return;
        }

        var userModel = userModelOptional.get();
        if (userModel.getUserType().equals(UserType.STUDENT.toString())) {
            errors.rejectValue("userInstructor", "UserIstructorError", "User must be INSTRUCTOR.");
        }
    }

}
