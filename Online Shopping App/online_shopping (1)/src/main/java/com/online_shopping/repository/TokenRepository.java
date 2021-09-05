package com.online_shopping.repository;


import com.online_shopping.token.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<ConfirmationToken, Integer> {
    ConfirmationToken findByToken(String confirmationToken);
    ConfirmationToken findTokenByUserId(int id);
}