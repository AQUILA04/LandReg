package com.optimize.common.entities.annotations.validator;

import com.optimize.common.entities.annotations.ConditionalNotNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class ConditionalNotNullValidator implements ConstraintValidator<ConditionalNotNull, Object> {

    private String booleanField;
    private String dependentField;

    @Override
    public void initialize(ConditionalNotNull constraintAnnotation) {
        this.booleanField = constraintAnnotation.booleanField();
        this.dependentField = constraintAnnotation.dependentField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Si l'objet entier est null, pas de validation
        }

        try {
            Class<?> clazz = value.getClass();
            Field booleanField = clazz.getDeclaredField(this.booleanField);
            Field dependentField = clazz.getDeclaredField(this.dependentField);

            booleanField.setAccessible(true);
            dependentField.setAccessible(true);

            Boolean condition = (Boolean) booleanField.get(value);
            Object dependentValue = dependentField.get(value);

            // Si le booleanField est true, dependentField doit être non-null
            if (Boolean.TRUE.equals(condition) && dependentValue == null) {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error accessing fields for validation", e);
        }

        return true;
    }
}
