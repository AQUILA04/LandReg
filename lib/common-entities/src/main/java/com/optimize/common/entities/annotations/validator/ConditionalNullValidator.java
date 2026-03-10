package com.optimize.common.entities.annotations.validator;

import com.optimize.common.entities.annotations.ConditionField;
import com.optimize.common.entities.annotations.ConditionalNull;
import com.optimize.common.entities.annotations.DependentField;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ConditionalNullValidator implements ConstraintValidator<ConditionalNull, Object> {

    private boolean isEnum;
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ConditionalNull constraintAnnotation) {
        this.isEnum = constraintAnnotation.isEnum();
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Objet globalement nul est valide
        }

        try {
            Field conditionField = null;
            String[] allowedValues = null;

            // Recherche du champ avec @ConditionField
            for (Field field : value.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(ConditionField.class)) {
                    conditionField = field;
                    allowedValues = field.getAnnotation(ConditionField.class).allowedValues();
                    break;
                }
            }

            if (conditionField == null || allowedValues == null) {
                throw new IllegalArgumentException("No field annotated with @ConditionField found.");
            }

            conditionField.setAccessible(true);
            Object conditionValue = conditionField.get(value);

            // Validation pour champ conditionnel (Enum ou String)
            boolean conditionMatches = false;
            if (isEnum && conditionValue != null) {
                if (!enumClass.isEnum()) {
                    throw new IllegalArgumentException("Provided class is not an enum: " + enumClass.getName());
                }
                String conditionValueStr = ((Enum<?>) conditionValue).name();
                conditionMatches = Arrays.asList(allowedValues).contains(conditionValueStr);
            } else if (!isEnum && conditionValue != null) {
                conditionMatches = Arrays.asList(allowedValues).contains(conditionValue.toString());
            }

            // Si la condition est remplie, les champs dépendants peuvent être null
            if (conditionMatches) {
                return true;
            }

            // Validation des champs dépendants
            for (Field field : value.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(DependentField.class)) {
                    field.setAccessible(true);
                    if (field.get(value) == null) {
                        return false; // Champ dépendant requis est null
                    }
                }
            }

            return true; // Tout est valide

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing fields for validation", e);
        }
    }
}
