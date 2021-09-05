package com.online_shopping.repository;

import com.online_shopping.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Role findByAuthority(String authority);
}
