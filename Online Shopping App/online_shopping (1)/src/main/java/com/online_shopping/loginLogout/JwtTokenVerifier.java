package com.online_shopping.loginLogout;

//import com.google.common.base.Strings;
import com.online_shopping.exception.InvalidTokenException;
import com.online_shopping.service.user.LogoutTokenService;
import com.online_shopping.service.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final LogoutTokenService logoutTokenService;
    private final UserService userService;

    @Autowired
    public JwtTokenVerifier(SecretKey secretKey, JwtConfig jwtConfig,
                            LogoutTokenService logoutTokenService,
                            UserService userService) {

        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.logoutTokenService = logoutTokenService;
        this.userService = userService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws InvalidTokenException, ServletException, IOException {

        String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());
        boolean authorizationHeaderIsNullOrEmpty= authorizationHeader==null;
        if(!authorizationHeaderIsNullOrEmpty) {
            authorizationHeaderIsNullOrEmpty = authorizationHeader.length()==0;
        }
        if ( authorizationHeaderIsNullOrEmpty  ||
                !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (!(logoutTokenService.isBlacklisted(token))  ) {
            try {
                Jws<Claims> claimsJws = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token);
                Claims body = claimsJws.getBody();
                String email = body.getSubject();
                var authorities = (List<Map<String, String>>) body.get("authorities");
                Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                        .map(role -> new SimpleGrantedAuthority(role.get("authority")))
                        .collect(Collectors.toSet());

                //System.out.println(simpleGrantedAuthorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email,
                                null, simpleGrantedAuthorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException ignored) {
            }

        }
        filterChain.doFilter(request, response);
    }


}
