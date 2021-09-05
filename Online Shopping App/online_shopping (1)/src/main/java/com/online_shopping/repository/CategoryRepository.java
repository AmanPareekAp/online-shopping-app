package com.online_shopping.repository;

import com.online_shopping.entity.product.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    List<Category> findByName(String name);
    Page<Category> findAll(Pageable pageable);
    Optional<Category> findById(int id);
}
