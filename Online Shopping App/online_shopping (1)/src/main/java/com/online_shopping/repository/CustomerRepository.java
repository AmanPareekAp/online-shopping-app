package com.online_shopping.repository;

import com.online_shopping.entity.user.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Page<Customer> findAll(Pageable pageable);
    Customer findByEmail(String email);
}
