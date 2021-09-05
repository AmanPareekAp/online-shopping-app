package com.online_shopping.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class EmailDto {

    @Email
    @NotEmpty
    private String email;

}
