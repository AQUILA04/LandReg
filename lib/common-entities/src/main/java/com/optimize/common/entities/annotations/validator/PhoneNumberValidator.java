package com.optimize.common.entities.annotations.validator;

import com.optimize.common.entities.annotations.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String EIGHT_DIGIT_PATTERN = "^(90|91|92|93|70|71|99|98|97|96|79|78)\\d{6}$";
    private static final String ELEVEN_DIGIT_PATTERN = "^228(90|91|92|93|70|71|99|98|97|96|79|78)\\d{6}$";

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null) {
            return false;
        }

        if (phoneNumber.length() == 8) {
            return phoneNumber.matches(EIGHT_DIGIT_PATTERN);
        } else if (phoneNumber.length() == 11) {
            return phoneNumber.matches(ELEVEN_DIGIT_PATTERN);
        }

        return false;
    }
}
