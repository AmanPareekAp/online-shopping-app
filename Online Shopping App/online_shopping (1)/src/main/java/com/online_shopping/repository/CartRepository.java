package com.online_shopping.repository;

import com.online_shopping.entity.order.Cart;
import com.online_shopping.entity.order.CustomerProductVariationKey;
import com.online_shopping.entity.user.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends CrudRepository<Cart, CustomerProductVariationKey> {

    List<Cart> findByCustomer(@Param("customer")Customer customer);

}
