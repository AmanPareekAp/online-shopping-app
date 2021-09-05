package com.online_shopping.repository;

import com.online_shopping.entity.product.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField,Integer> {
    CategoryMetadataField findByName(String name);
    Page<CategoryMetadataField> findAll(Pageable pageable);
}
