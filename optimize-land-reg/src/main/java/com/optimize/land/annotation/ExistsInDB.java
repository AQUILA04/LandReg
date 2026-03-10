package com.optimize.land.annotation;

import com.optimize.land.annotation.validator.ExistsInDBValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistsInDBValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsInDB {

    String message() default "Value does not exist in the database";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entity(); // La classe de l'entité dans laquelle chercher

    String field(); // Le champ de l'entité à vérifier
}
