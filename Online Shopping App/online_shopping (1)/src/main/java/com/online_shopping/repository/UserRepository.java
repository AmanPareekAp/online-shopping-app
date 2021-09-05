package com.online_shopping.repository;

import com.online_shopping.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);

    @Query("from User u where u.email=:email and u.password=:password")
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("UPDATE User u SET u.failedAttempt = ?1,u.isUnlocked=?2 WHERE u.email = ?3")
    @Modifying
    public void updateFailedAttempts(int failAttempts,boolean isUnlocked, String email);
}

