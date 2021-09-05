package com.online_shopping.dto;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class PasswordDto {

    @NotEmpty
    @Pattern(regexp = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" +
            "(?=.*[@#$%^&+=!])" + ".{8,15}$")
    String password;

    String confirmPassword;

    public boolean isSame(){
        return password.equals(confirmPassword);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
