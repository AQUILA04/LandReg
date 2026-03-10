package com.optimize.common.entities.annotations;

import com.optimize.common.entities.annotations.validator.ConditionalNullValidator;
import com.optimize.common.entities.enums.State;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ConditionalNullValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalNull {

    String message() default "Invalid field combination based on conditions";

    // Nom du champ qui détermine la condition
//    String conditionField();
    // Indique si le champ conditionnel est une énumération
    boolean isEnum() default false;

    // Classe de l'énumération si applicable
    Class<? extends Enum<?>> enumClass() default State.class;

    // Valeurs conditionnelles pour lesquelles les champs dépendants peuvent être null
//    String[] allowedValues();
//
//    // Champs qui doivent être vérifiés pour null/non-null
//    String[] dependentFields();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
