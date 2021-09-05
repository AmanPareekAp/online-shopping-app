package com.online_shopping.dto;

import com.online_shopping.entity.product.Category;
import com.online_shopping.entity.product.CategoryMetadataField;
import com.online_shopping.entity.product.CategoryMetadataFieldValues;
import com.online_shopping.entity.product.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class CategoryViewDto {

    private int id;
    private String name;
    private String parentCategory;
    private List<ChildCategoryViewDto> childCategoryList;
    private List<CategoryMetadataFieldViewDto> categoryMetadataList;
    private List<ProductViewDto> productList;


    public CategoryViewDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        if(category.getParentCategory()!=null) {
            parentCategory=category.getParentCategory().getName();
        }
        this.childCategoryList = getChildCategoryListWithoutLooping(category.getChildCategoryList());
        this.categoryMetadataList = getCategoryMetadataFieldViewDtoWithoutLooping(
                        category.getCategoryMetadataFieldValuesList());
        this.productList = getProductListWithoutLooping(category.getProductList());
    }

    public List<ProductViewDto> getProductListWithoutLooping(List<Product> productList){

        if(productList==null)
            return null;

        List<ProductViewDto> productViewDtoList = new ArrayList<>();

        for (Product product : productList)
        {
            productViewDtoList.add(new ProductViewDto(product));
        }

        return productViewDtoList;

    }

    public List<CategoryMetadataFieldViewDto> getCategoryMetadataFieldViewDtoWithoutLooping(
            List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList)
    {
        if(categoryMetadataFieldValuesList==null)
            return null;
        Set<CategoryMetadataField> categoryMetadataFieldSet = new HashSet<>();

        for(CategoryMetadataFieldValues categoryMetadataFieldValues :
                categoryMetadataFieldValuesList)
        {
            categoryMetadataFieldSet.add(categoryMetadataFieldValues.getCategoryMetadataField());
        }

        List<CategoryMetadataFieldViewDto> categoryMetadataFieldViewDtoList = new ArrayList<>();

        for(CategoryMetadataField categoryMetadataField : categoryMetadataFieldSet)
        {
            categoryMetadataFieldViewDtoList.add(new CategoryMetadataFieldViewDto(categoryMetadataField.getName(),
                    categoryMetadataField.getCategoryMetadataFieldValuesList()));
        }

        return categoryMetadataFieldViewDtoList;

    }

    public List<ChildCategoryViewDto> getChildCategoryListWithoutLooping(List<Category> childCategoryList)
    {
        List<ChildCategoryViewDto> resultChildCategoryList = new ArrayList<>();

        for(Category tempCategory : childCategoryList)
        {
            Category category = new Category(tempCategory.getName());
            category.setId(tempCategory.getId());
            resultChildCategoryList.add(new ChildCategoryViewDto(category));

        }

        return resultChildCategoryList;
    }

}
