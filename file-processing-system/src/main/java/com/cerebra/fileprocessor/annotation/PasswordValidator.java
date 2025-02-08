package com.cerebra.fileprocessor.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (password.length() > 15) {
            return false;
        }

        String regex = "^(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]+$";
        return password.matches(regex);
    }
}
