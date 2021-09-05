package com.online_shopping.service.Dto;

import com.online_shopping.dto.CategoryMetadataFieldValuesViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerFilterDto {


    List<CategoryMetadataFieldValuesViewDto> categoryFilters;
    int minPrice = 0;
    int maxPrice =0;
    List<String> brandFilters;

    public CustomerFilterDto(List<CategoryMetadataFieldValuesViewDto> categoryFilters,
                             List<String> brandFilters,
                             int minPrice,
                             int maxPrice) {

        this.categoryFilters = categoryFilters;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.brandFilters = brandFilters;

    }
}
