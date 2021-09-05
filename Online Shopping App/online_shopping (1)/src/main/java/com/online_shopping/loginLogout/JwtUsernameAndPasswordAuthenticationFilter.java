package com.online_shopping.loginLogout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online_shopping.entity.user.User;
import com.online_shopping.exception.RequestBodyException;
import com.online_shopping.exception.UserInactiveException;
import com.online_shopping.exception.UserNotFoundException;
import com.online_shopping.service.user.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private SecretKey secretKey;
    private final UserService userService;
    private String email;
    private String password;

    @Autowired
    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                      JwtConfig jwtConfig,
                                                      SecretKey secretKey,
                                                      UserService userService) {

        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
        this.userService = userService;
    }


    @Override
    @Transactional
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            UsernameAndPasswordAuthenticationRequest emailAndPassword =
                    new ObjectMapper().readValue(request.getInputStream(),
                            UsernameAndPasswordAuthenticationRequest.class);
            email = emailAndPassword.getEmail();
            password = emailAndPassword.getPassword();

            if((email==null || email.length()==0) && (password==null || password.length()==0))
            {
                throw new RequestBodyException("Either password or email is null or empty");
            }

            User user = userService.getUserByEmail(email);

            if(user==null)
            {
                throw new UserNotFoundException("User with entered email id does not exist");
            }
            if(!user.isUnlocked())
            {
                System.out.println("account locked");
                throw new AuthenticationException("Account is locked wait for 24 hours and try again") {
                    @Override
                    public String getMessage() {
                        return super.getMessage();
                    }
                };
            }
            if(!userService.checkUserStatus(user))
            {
                throw new UserInactiveException("User inactive or deleted");
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    emailAndPassword.getEmail(), emailAndPassword.getPassword());
            return authenticationManager.authenticate(authentication);
        } catch (IOException exception) {
            throw new RequestBodyException("Error reading the request body.");
        }
    }

    @Override
    @Transactional
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        String authEmail = authResult.getName();
        User user = userService.getUserByEmail(authEmail);
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(authEmail);
        }

        String token = Jwts.builder()
                .setSubject(authEmail)
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now()
                .plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(secretKey)
                .compact();

        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);

        new ObjectMapper().writeValue(response.getWriter(),"You have logged in successfully," +
                " check header for login token");

        //System.out.println(authEmail + " logged in");
    }

    @Override
    @Transactional
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        logger.info("Invalid Credentials, Email Id : " + email );
        User user = userService.getUserByEmail(email);

        if (user != null) {
            if (user.isActive() && user.isUnlocked()) {
                if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1) {
                    userService.increaseFailedAttempts(user);
                    failed = new BadCredentialsException("Enter Correct Password");
                } else {
                    userService.lock(user);
                    failed = new LockedException("" +
                            "Your account is locked due to 3 repeated unsuccessful attempts," +
                            "Try again after 24 hours");
                }

            }
            else if (!user.isUnlocked()) {
                if (userService.unlockWhenTimeExpired(user)) {
                    failed = new LockedException("Your account has been unlocked. Please try to login again.");
                }
            }
        }
        else {

            failed = new BadCredentialsException("User with entered email id does not exist");
        }

        new ObjectMapper().writeValue(response.getWriter(),failed.getMessage());

    }
}
