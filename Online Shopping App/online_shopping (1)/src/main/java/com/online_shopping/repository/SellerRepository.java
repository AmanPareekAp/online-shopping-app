package com.online_shopping.repository;

import com.online_shopping.entity.user.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Integer> {

    Page<Seller> findAll(Pageable pageable);
    Seller findByEmail(String email);
}
