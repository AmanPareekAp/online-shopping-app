package com.online_shopping.service.user;


import com.online_shopping.entity.user.Role;
import com.online_shopping.entity.user.User;
import com.online_shopping.repository.UserRepository;
import com.online_shopping.securityconfig.AppUser;
import com.online_shopping.securityconfig.GrantAuthorityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    UserRepository userRepository;

    @Transactional
    UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (email != null) {
            List<GrantAuthorityImpl> grantAuthorityList=new ArrayList<>();
            List<Role> roles=user.getRoleList();
            for(Role role: roles)
            {
                grantAuthorityList.add(new GrantAuthorityImpl(role.getAuthority()));

            }
            return new AppUser(user.getEmail(), user.getPassword(), user.isActive(), grantAuthorityList);
        }
        else {
            throw new RuntimeException();
        }

    }
}
