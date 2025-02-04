package com.increff.commons.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupForm {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
} 