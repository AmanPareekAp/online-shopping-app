package com.online_shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldValuesDto {

    @NotNull
    private Integer categoryMetadataFieldId;
    @NotNull
    private Integer categoryId;
    @NotEmpty
    private String metadataValues;

}
