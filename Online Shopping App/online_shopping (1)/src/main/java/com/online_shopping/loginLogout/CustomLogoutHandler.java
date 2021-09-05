package com.online_shopping.loginLogout;


import com.online_shopping.repository.LogoutTokenRepository;
import com.online_shopping.service.user.LogoutTokenService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CustomLogoutHandler implements LogoutSuccessHandler {

    @Autowired
    private LogoutTokenService logoutTokenService;
    @Autowired
    private LogoutTokenRepository logoutTokenRepository;


    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header != null && header.length()!=0) {
            String token = header.substring(7);

            if(logoutTokenRepository.findByToken(token)!=null)
            {
                new ObjectMapper().writeValue(response.getWriter(),
                        "You have already been logged out.");
            }


            logoutTokenService.saveLogoutToken(token);
            new ObjectMapper().writeValue(response.getWriter(),
                    "You have been logged out successfully.");
        }
        else
        {
            throw new RuntimeException("Enter valid header, header maybe null or empty");
        }
    }
}
