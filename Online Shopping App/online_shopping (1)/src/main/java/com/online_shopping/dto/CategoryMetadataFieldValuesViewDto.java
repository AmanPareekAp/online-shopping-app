package com.online_shopping.dto;

import lombok.Data;

@Data

public class CategoryMetadataFieldValuesViewDto {

    String metadataValue;

    public CategoryMetadataFieldValuesViewDto(String metadataValue) {
        this.metadataValue = metadataValue;
    }
}


