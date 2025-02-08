package com.cerebra.fileprocessor.annotation;
import java.lang.annotation.*;
import jakarta.validation.Payload;
import jakarta.validation.Constraint;
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {
    String message() default "Null or Blank value not allowed";
    int min() default 0;
	int max() default 0;
	String isMandatory() default "yes";
    String isEmail() default "no";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}
