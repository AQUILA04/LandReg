package com.optimize.common.entities.annotations.validator;

import com.optimize.common.entities.annotations.NonEmptyByteArray;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NonEmptyByteArrayValidator implements ConstraintValidator<NonEmptyByteArray, byte[]> {

    @Override
    public void initialize(NonEmptyByteArray constraintAnnotation) {
        // Méthode optionnelle pour initialiser des paramètres si nécessaire
    }

    @Override
    public boolean isValid(byte[] value, ConstraintValidatorContext context) {
        // Valide que le tableau n'est ni null ni vide
        return value != null && value.length > 0;
    }
}
