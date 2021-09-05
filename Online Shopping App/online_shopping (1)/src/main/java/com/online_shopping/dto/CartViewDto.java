package com.online_shopping.dto;

import com.online_shopping.entity.order.Cart;
import lombok.Data;

@Data
public class CartViewDto {

    private ProductVariationViewDto productVariation;
    private int quantity;

    public CartViewDto(Cart cart) {
        this.productVariation = new ProductVariationViewDto(cart.getProductVariation());
        this.quantity = cart.getQuantity();
    }
}
