package com.optimize.common.entities.annotations;

import com.optimize.common.entities.annotations.validator.Base64ImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = Base64ImageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64Image {

    String message() default "Invalid Base64 image format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
