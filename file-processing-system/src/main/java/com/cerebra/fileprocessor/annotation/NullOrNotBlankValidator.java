package com.cerebra.fileprocessor.annotation;
import com.cerebra.fileprocessor.common.EmailValidator;
import com.cerebra.fileprocessor.common.ValidationUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {
	private int min;
    private int max;
    private String isMandatory;
    private String isEmail;
	@Override
	public void initialize(NullOrNotBlank parameters) {
		min = parameters.min();
        max = parameters.max();
        isMandatory=parameters.isMandatory();
        isEmail=parameters.isEmail();
	}

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (ValidationUtil.isBlank(value)) {
            return !isMandatory.equals("yes");
        }

        if (isEmail.equals("yes") && !EmailValidator.isValidEmail(value)) {
            return false;
        }
        int length = value.length();
        return length >= min && length <= max;
    }

}
