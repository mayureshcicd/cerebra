package com.cerebra.fileprocessor.dto;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInRequest implements Serializable {
    String email;
    String password;
}