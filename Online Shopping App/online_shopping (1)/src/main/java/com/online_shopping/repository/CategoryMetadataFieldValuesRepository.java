package com.online_shopping.repository;


import com.online_shopping.entity.product.CategoryMetadataFieldKey;
import com.online_shopping.entity.product.CategoryMetadataFieldValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValues,
        CategoryMetadataFieldKey> {

    @Query("from CategoryMetadataFieldValues  where category_metadata_field_id = ?1 and category_id = ?2")
    CategoryMetadataFieldValues findById(int categoryMetadataFieldId, int categoryId);
}
