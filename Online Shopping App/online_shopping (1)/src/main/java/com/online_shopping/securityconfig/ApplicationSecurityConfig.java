package com.online_shopping.securityconfig;


import com.online_shopping.loginLogout.JwtConfig;
import com.online_shopping.loginLogout.JwtTokenVerifier;
import com.online_shopping.loginLogout.JwtUsernameAndPasswordAuthenticationFilter;
import com.online_shopping.service.user.AppUserDetailsService;
import com.online_shopping.service.user.LogoutTokenService;
import com.online_shopping.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.crypto.SecretKey;

//@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private SecretKey secretKey;
    @Autowired
    private AppUserDetailsService appUserDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    LogoutTokenService logoutTokenService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(appUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(),
                        jwtConfig, secretKey, userService))
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig,logoutTokenService,userService),
                        JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
              //  .antMatchers("/").anonymous()
                .antMatchers("/customer/register").anonymous()
                .antMatchers("/seller/register").anonymous()
                .antMatchers("/forgot-password").anonymous()
                .antMatchers("/confirm-reset").anonymous()
                .antMatchers("/customer/confirm-account").anonymous()
                .antMatchers("/login").anonymous()
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/customer/**").hasAnyRole("CUSTOMER")
                .antMatchers("/seller/**").hasAnyRole("SELLER")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout().logoutSuccessHandler(logoutSuccessHandler);

    }

}
