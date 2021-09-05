package com.online_shopping.loginLogout;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UsernameAndPasswordAuthenticationRequest {
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String password;
}
