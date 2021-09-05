package com.online_shopping.dto;

import com.online_shopping.entity.product.CategoryMetadataFieldValues;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryMetadataFieldViewDto {

    String name;
    private List<CategoryMetadataFieldValuesViewDto> categoryMetadataFieldValuesViewDtoList;

    public CategoryMetadataFieldViewDto(String name,
                                        List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList)
    {
        this.name=name;
        categoryMetadataFieldValuesViewDtoList = new ArrayList<>();
        for(CategoryMetadataFieldValues categoryMetadataFieldValues :
                categoryMetadataFieldValuesList)
        {
            categoryMetadataFieldValuesViewDtoList.add(
                    new CategoryMetadataFieldValuesViewDto(categoryMetadataFieldValues.getMetadataValues()));
        }
    }

}
