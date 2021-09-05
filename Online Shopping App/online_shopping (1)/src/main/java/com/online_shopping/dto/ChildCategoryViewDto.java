package com.online_shopping.dto;

import com.online_shopping.entity.product.Category;
import lombok.Data;

@Data
public class ChildCategoryViewDto {

    private int id;
    private String name;

    public ChildCategoryViewDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
