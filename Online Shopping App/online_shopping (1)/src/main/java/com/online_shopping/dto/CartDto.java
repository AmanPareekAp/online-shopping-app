package com.online_shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    @NotNull
    private Integer productVariationId;
    @NotNull
    private Integer quantity;
    private boolean isWishlistItem;

}
