package com.online_shopping.repository;

import com.online_shopping.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<User,Integer> {


}
