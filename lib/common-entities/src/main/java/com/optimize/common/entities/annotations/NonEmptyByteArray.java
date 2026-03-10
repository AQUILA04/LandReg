package com.optimize.common.entities.annotations;

import com.optimize.common.entities.annotations.validator.NonEmptyByteArrayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NonEmptyByteArrayValidator.class) // Lien avec le validateur
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Où l'annotation peut être utilisée
@Retention(RetentionPolicy.RUNTIME) // Durée de vie de l'annotation
public @interface NonEmptyByteArray {
    String message() default "Le tableau de bytes ne doit pas être null ni vide.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
