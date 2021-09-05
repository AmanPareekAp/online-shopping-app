package com.online_shopping.service.user;



import com.online_shopping.repository.LogoutTokenRepository;
import com.online_shopping.token.LogoutToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogoutTokenService {
    @Autowired
    private LogoutTokenRepository logoutTokenRepository;

    public void saveLogoutToken(String token) {
        logoutTokenRepository.save(new LogoutToken(token));
    }

    public boolean isBlacklisted(String token) {
        return logoutTokenRepository.findByToken(token) != null;
    }

}
