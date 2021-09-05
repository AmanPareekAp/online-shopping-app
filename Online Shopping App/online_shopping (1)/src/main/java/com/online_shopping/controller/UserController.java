package com.online_shopping.controller;


import com.online_shopping.dto.PasswordDto;
import com.online_shopping.repository.TokenRepository;
import com.online_shopping.service.user.EmailSenderService;
import com.online_shopping.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/admin/home")
    public String adminHome(){
        return "Admin home";
    }

    @GetMapping("/customer/home")
    public String customerHome(){
        return "Customer home";
    }

    @GetMapping("/seller/home")
    public String sellerHome(){
        return "Seller home";
    }

    //forget-password-apis

    @ApiOperation(value = "Forgot Password API")
    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestParam("username") String username){

        return userService.sendResetPasswordLinkToUser(username);
    }

    @ApiOperation(value = "Forgot Password Confirmation Token API")
    @PostMapping("/confirm-reset")
    public ResponseEntity<Object> validateResetToken(@RequestParam("token")String token,
                                                     @RequestBody PasswordDto passwordDto)
                                                     throws Exception {

        return userService.resetPasswordIfTokenIsValid(token, passwordDto.getPassword()
                ,passwordDto.getConfirmPassword());
    }



}