package com.optimize.common.entities.annotations;

import com.optimize.common.entities.annotations.validator.ConditionalNotNullValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ConditionalNotNullValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalNotNull {

    String message() default "Field must not be null when condition is true";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String booleanField(); // Nom de l'attribut boolean

    String dependentField(); // Nom de l'attribut dépendant
}
