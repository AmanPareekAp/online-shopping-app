package com.online_shopping.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.online_shopping.entity.product.ProductVariation;
import lombok.Data;

@Data
@JsonFilter("ProductVariationViewFilter")
public class ProductVariationViewDto {


    private int id;
    private ProductViewDto product;
    private int quantityAvailable;
    private int price;
    private boolean isActive;
    private String metadataString;

    public ProductVariationViewDto(ProductVariation productVariation) {
        this.id = productVariation.getId();
        this.product = new ProductViewDto(productVariation.getProduct());
        this.quantityAvailable = productVariation.getQuantityAvailable();
        this.price = productVariation.getPrice();
        this.isActive = productVariation.isActive();
        this.metadataString = productVariation.getMetadataString();
    }
}
