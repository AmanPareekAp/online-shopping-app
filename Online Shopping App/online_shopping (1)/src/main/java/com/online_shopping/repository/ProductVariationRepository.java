package com.online_shopping.repository;

import com.online_shopping.entity.product.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, Integer> {
}
