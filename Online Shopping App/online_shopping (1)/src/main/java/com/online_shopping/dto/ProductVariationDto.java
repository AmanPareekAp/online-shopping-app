package com.online_shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariationDto {

    @NotNull
    private Integer productId;
    @NotNull
    private Integer quantityAvailable;
    @NotNull
    private Integer price;
    @NotEmpty
    private Map<String, String> metadataMap;

}
