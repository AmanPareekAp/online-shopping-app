package com.online_shopping.repository;

import com.online_shopping.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    //Product findByNameBrandAndCategory(String name, String brand, Category category);
}
