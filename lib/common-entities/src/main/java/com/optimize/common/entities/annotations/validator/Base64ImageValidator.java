package com.optimize.common.entities.annotations.validator;

import com.optimize.common.entities.annotations.Base64Image;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class Base64ImageValidator implements ConstraintValidator<Base64Image, String> {

    @Override
    public boolean isValid(String base64Image, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(base64Image)) {
            return false;
        }

        boolean containsBase64 = base64Image.contains(";base64,");
        int commaIndex = base64Image.indexOf(",");
        boolean validCommaPosition = (commaIndex == 21 || commaIndex == 22);
        boolean validLength = base64Image.length() > 1040;

        return containsBase64 && validCommaPosition && validLength;
    }
}
