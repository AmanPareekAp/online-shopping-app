package com.online_shopping.controller;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.online_shopping.dto.*;
import com.online_shopping.entity.user.Address;
import com.online_shopping.entity.user.Seller;
import com.online_shopping.service.user.SellerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Api(value = "APIs Related to Seller")
public class SellerController {

    @Autowired
    SellerService sellerService;

    /* <---- SELLER REGISTRATION APIS -----> */

    @ApiOperation(value = "Seller Registration")
    @PostMapping("/seller/register")
    public ResponseEntity<Object> registerSeller(@Valid @RequestBody
                                                   SellerDto sellerDto,
                                                   HttpServletRequest request) {

        sellerService.register(sellerDto);
        return new ResponseEntity<>("Your account created successfully," +
                " please check you email for confirmation," +
                " your account will activated by admin shortly", HttpStatus.OK);
    }

    /* <---- SELLER APIS -----> */

    @ApiOperation(value = "View a seller profile")
    @GetMapping("/seller/view-profile")
    public SellerViewDto viewProfile(Principal user)
            throws Exception {
        return sellerService.viewProfile(user.getName());
    }

    @ApiOperation(value = "Update a seller profile")
    @PatchMapping("/seller/update-profile")
    public ResponseEntity<Object> updateProfile(Principal user,
                                                @RequestBody Seller seller){

        return sellerService.updateProfile(user.getName(), seller);

    }

    @ApiOperation(value = "Update a seller password")
    @PatchMapping("/seller/update-password")
    public ResponseEntity<Object> updatePassword(Principal user,
                                                 @RequestBody PasswordDto passwordDto)
                                                    throws Exception {

        return sellerService.updatePassword(user.getName(), passwordDto);
    }

    @ApiOperation(value = "Update seller address")
    @PatchMapping("/seller/update-address")
    public ResponseEntity<Object> updateAddress(Principal user,
                                                @RequestBody Address address){

        return sellerService.updateAddresses(user.getName(), address);
    }

    /* <---- CATEGORY APIS -----> */

    @ApiOperation(value = "View a list of available Category")
    @GetMapping("/seller/get-category-list")
    public Iterable<CategoryViewDto> getCategories() {
        Iterable<CategoryViewDto> categoryViewDtoList = sellerService.getCategoryList();

        return categoryViewDtoList;
    }

    /* <---- PRODUCT APIS -----> */

    @ApiOperation(value = "Add a product")
    @PostMapping("/seller/add-product")
    public ResponseEntity<Object> addProduct(Principal user,
             @Valid @RequestBody ProductDto productDto){

        return sellerService.addProduct(user.getName(),productDto);
    }

    @ApiOperation(value = "View a available product")
    @GetMapping("seller/get-product")
    public MappingJacksonValue viewProduct(Principal user, @RequestParam int id){

        ProductViewDto productViewDto = sellerService.viewProductById(user.getName(), id);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productViewDto);
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "brand",
                "category","isCancellable", "isReturnable");
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("ProductViewFilter", filter1);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    @ApiOperation(value = "View a list of available products")
    @GetMapping("/seller/get-product-list")
    public MappingJacksonValue viewProductList(Principal principal) {
        List<ProductViewDto> productViewDtoList = sellerService.viewAllProductDetails(principal.getName());
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productViewDtoList);
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "brand",
                "category","isCancellable", "isReturnable");
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("ProductViewFilter", filter1);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    @ApiOperation(value = "Delete a product")
    @DeleteMapping("/seller/delete-product")
    public ResponseEntity<String> deleteProduct(@RequestParam int id,Principal user) {
        return sellerService.deleteProduct(id,user.getName());

    }

    @ApiOperation(value = "Update a product")
    @PutMapping("/seller/update-product")
    public ResponseEntity<String> updateProduct(Principal user,@RequestParam int id,
                                                 @RequestBody ProductDto productDto) {
        return sellerService.updateProduct(productDto, id, user.getName());

    }

    /* <---- PRODUCT VARIATION APIS -----> */

    @ApiOperation(value = "Add a product variation")
    @PostMapping("/seller/add-product-variation")
    public ResponseEntity<Object> addProductVariation(Principal user,
                                                      @Valid @RequestBody
                                                              ProductVariationDto productVariationDto){

        return sellerService.addProductVariation(user.getName(),productVariationDto);
    }

    @ApiOperation(value = "View a product variation with Product variation ID")
    @GetMapping("/seller/get-product-variation")
    public MappingJacksonValue viewProductVariation(@RequestParam int productVariationId ,
                                                    Principal user) {

        ProductVariationViewDto productVariationViewDto =
                sellerService.getProductVariation(productVariationId,user.getName());
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productVariationViewDto);
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "brand",
                "category");
        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("id",
                "metadataString","product");
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("ProductViewFilter", filter1)
                .addFilter("ProductVariationViewFilter", filter2);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    @ApiOperation(value = "View list of product variation")
    @GetMapping("/seller/get-product-variation-list")
    public MappingJacksonValue viewProductVariationList(Principal user) {

        List<ProductVariationViewDto> productVariationViewDtoList =
                sellerService.getProductVariationList(user.getName());
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productVariationViewDtoList);
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "brand",
                "category");
        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("id",
                "metadataString","product");
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("ProductViewFilter", filter1)
                .addFilter("ProductVariationViewFilter", filter2);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    @ApiOperation(value = "Update a product variation")
    @PatchMapping("/seller/update-product-variation")
    public ResponseEntity<Object> updateAddress(@Valid @RequestBody
                                                 ProductVariationUpdateDto productVariationUpdateDto,
                                                Principal user){

        return sellerService.updateProductVariation(user.getName(), productVariationUpdateDto);
    }

    /* <---- ORDER APIS -----> */

    @ApiOperation(value = " View seller's ordered product variation list")
    @GetMapping("/seller/get-order-list/{pageSize}")
    public MappingJacksonValue getOrderList(Principal user, @PathVariable int pageSize){

        Page<OrderProductViewDto> orderProductViewDtoPage = sellerService.getOrderList(user.getName(),pageSize);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(orderProductViewDtoPage.getContent());
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "brand",
                "category");
        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("id",
                "metadataString","product");
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("ProductViewFilter", filter1)
                .addFilter("ProductVariationViewFilter", filter2);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }


    @ApiOperation(value = " Change Order state of an ordered product owned by the same seller ")
    @PutMapping("/seller/change-order-state")
    public ResponseEntity changeOrderState( Principal user,
                                            @Valid @RequestBody OrderStateChangeDto orderStateChangeDto){

        return sellerService.changeOrderState(user.getName(),orderStateChangeDto);
    }
}
