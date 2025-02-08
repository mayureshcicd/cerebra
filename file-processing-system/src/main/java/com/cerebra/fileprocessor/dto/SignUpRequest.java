package com.cerebra.fileprocessor.dto;

import com.cerebra.fileprocessor.annotation.NullOrNotBlank;
import com.cerebra.fileprocessor.annotation.ValidPassword;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequest implements Serializable {

    @NullOrNotBlank(min = 1, max = 250, message = "First Name is required and  must be between {min} and {max} characters")
    private String firstName;

    @NullOrNotBlank(min = 5, max = 250, isEmail="yes",message = "Valid email address is required and must be between {min} and {max} characters long.")
    private String email;

    @ValidPassword(message = "New Password must be 8 characters long, contain at least one letter, one number, and one special character")
    private String newPassword;

    @ValidPassword(message = "Confirm Password must be 8 characters long, contain at least one letter, one number, and one special character")
    private String confirmPassword;

}

