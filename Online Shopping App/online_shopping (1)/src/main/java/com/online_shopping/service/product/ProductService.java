package com.online_shopping.service.product;

import com.online_shopping.entity.product.Category;
import com.online_shopping.entity.product.CategoryMetadataField;
import com.online_shopping.entity.product.CategoryMetadataFieldValues;
import com.online_shopping.entity.product.Product;
import com.online_shopping.entity.user.Seller;
import com.online_shopping.exception.CategoryException;
import com.online_shopping.repository.CategoryMetadataFieldRepository;
import com.online_shopping.repository.CategoryMetadataFieldValuesRepository;
import com.online_shopping.repository.ProductVariationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Autowired
    ProductVariationRepository productVariationRepository;

    public Map<String, String> convertStringToMap(String mapAsString) {
        Map<String, String> map = Arrays.stream(mapAsString.split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
        return map;
    }


    public boolean checkMapValidity(Map<String, String> map, Product product) {

        for (Map.Entry<String, String> entry : map.entrySet()) {

            System.out.println("key : " +  entry.getKey());
            System.out.println("value : " +  entry.getValue());

            CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository.
                    findByName(entry.getKey());

            if (categoryMetadataField == null) {
                throw new CategoryException("Category metadata field with name:" + entry.getKey()
                        +" does not exist.");
            }

            Category category = product.getCategory();

            CategoryMetadataFieldValues categoryMetadataFieldValues =
                    categoryMetadataFieldValuesRepository
                    .findById(categoryMetadataField.getId(),category.getId());

            String metadataValues=categoryMetadataFieldValues.getMetadataValues();

            if(!metadataValues.contains(entry.getValue()))
                return false;

            }

        return true;
    }

    public boolean checkProductStatus(Product product) {

        return (!product.isDeleted()) && product.isActive();
    }

    public boolean checkOwner(Seller seller, int productId) {

        boolean isSellerProductOwner=false;
        for(Product product : seller.getProductList())
        {
            if(product.getId()==productId) {
                isSellerProductOwner = true;
            }
        }

        return isSellerProductOwner;
    }
}
