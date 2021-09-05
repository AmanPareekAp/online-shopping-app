package com.online_shopping.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.online_shopping.entity.product.Product;
import lombok.Data;

@Data
@JsonFilter("ProductViewFilter")
public class ProductViewDto {

    private int id;

    private String name;
    private String description="Not available";
    private boolean isCancellable;
    private boolean isReturnable;
    private String brand;
    private boolean isActive;
    private String category;

    public ProductViewDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        if(product.getDescription()!=null)
        this.description = product.getDescription();
        this.isCancellable = product.isCancellable();
        this.isReturnable = product.isReturnable();
        this.brand = product.getBrand();
        this.isActive = product.isActive();
        this.category = product.getCategory().getName();
    }
}
