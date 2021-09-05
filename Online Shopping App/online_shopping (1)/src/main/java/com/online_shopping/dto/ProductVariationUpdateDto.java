package com.online_shopping.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class ProductVariationUpdateDto {

    @NotNull
    private Integer id;
    private Integer productId;
    private Integer quantityAvailable;
    private Integer price;
    private Map<String, String> metadataMap;
}
