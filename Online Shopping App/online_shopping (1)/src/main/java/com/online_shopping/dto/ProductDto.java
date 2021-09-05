package com.online_shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Integer id;
    @NotEmpty
    private String name;
    private String description;
    private boolean isCancellable;
    private boolean isReturnable;
    @NotEmpty
    private String brand;
    @NotNull
    private Integer categoryId;
    private int sellerId;
}