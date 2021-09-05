package com.online_shopping.controller;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.online_shopping.dto.*;
import com.online_shopping.service.user.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Api(value = "APIs Related to Admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    AdminService adminService;

    /* <---- ADMIN APIS -----> */

    @ApiOperation(value = "View a list of available customers")
    @GetMapping("/admin/get-customers")
    public Iterable<CustomerViewDto> getCustomerDetails() {

        return adminService.getCustomers();
    }

    @ApiOperation(value = "View a list of available sellers")
    @GetMapping("/admin/get-sellers")
    public Iterable<SellerViewDto> getSellerDetails() {
        return adminService.getSellers();
    }

    @ApiOperation(value = "Activate Customer with ID")
    @PutMapping("/admin/activate-customer")
    public ResponseEntity<String> activateCustomer(@RequestParam int id){
        return adminService.activateCustomer(id);
    }

    @ApiOperation(value = "Deactivate Customer with ID")
    @PutMapping("/admin/deactivate-customer")
    public ResponseEntity<String> deActivateCustomer(@RequestParam int id){
        return adminService.deActivateCustomer(id);
    }

    @ApiOperation(value = "Unlock Customer with ID")
    @PutMapping("/admin/unlock-customer")
    public ResponseEntity<String> unlockCustomer( @RequestParam int id) {
        return adminService.unlockCustomer(id);
    }

    @ApiOperation(value = "Activate Seller with ID")
    @PutMapping("/admin/activate-seller")
    public ResponseEntity<String> activateSeller(@RequestParam int id){
        return adminService.activateSeller(id);
    }

    @ApiOperation(value = "Deactivate Seller with ID")
    @PutMapping("/admin/deactivate-seller")
    public ResponseEntity<String> deActivateSeller( @RequestParam int id){
        return adminService.deActivateSeller(id);
    }

    @ApiOperation(value = "Unlock Seller with ID")
    @PutMapping("/admin/unlock-seller")
    public ResponseEntity<String> unlockSeller( @RequestParam int id) {
        return adminService.unlockSeller(id);
    }


    /* <---- CATEGORY APIS -----> */

    @ApiOperation(value = "Add a Category Metadata Field")
    @PostMapping("/admin/add-category-metadata-field")
    public ResponseEntity<String> addMetadataField(@Valid @RequestBody
                                                   CategoryMetadataFieldDto categoryMetadataFieldDto) {

        String name = adminService.addCategoryMetadataField(categoryMetadataFieldDto);
        return new ResponseEntity<>(name + " category Metadata Field added" , HttpStatus.OK);
    }

    @ApiOperation(value = "Get Category Metadata Field List")
    @GetMapping("/admin/get-category-metadata-field-list")
    public MappingJacksonValue getCategoryMetadataFiledList(){

        return adminService.getCategoryMetadataFiledList();
    }

    @ApiOperation(value = "Add a Category")
    @PostMapping("/admin/add-category")
    public ResponseEntity<String> addNewCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return adminService.addCategory(categoryDto);
    }

    @ApiOperation(value = "Get a available Category")
    @GetMapping("/admin/get-category")
    public CategoryViewDto getCategory(@RequestParam int id) {
       return adminService.getCategory(id);
    }

    @ApiOperation(value = "View a list of available Category")
    @GetMapping("/admin/get-category-list")
    public List<CategoryViewDto> getCategoryList() {
        return adminService.getCategoryList();
    }


    @ApiOperation(value = "Update a category")
    @Transactional
    @PutMapping("/admin/update-category")
    public ResponseEntity<Object> updateCategory(@RequestParam int categoryId,
                                                 @Valid @RequestBody CategoryDto categoryDto){

        return adminService.updateCategory(categoryId,categoryDto);

    }

    @ApiOperation(value = "Add a Category Metadata Field Values")
    @PostMapping("/admin/add-category-metadata-field-values")
    public ResponseEntity<Object> addMetadataValues(@Valid @RequestBody CategoryMetadataFieldValuesDto
                                                            categoryMetadataFieldValuesDto) {
       return adminService.addCategoryMetadataFieldValues(categoryMetadataFieldValuesDto);
    }

    @ApiOperation(value = "Update a Category Metadata Field Values")
    @Transactional
    @PutMapping("/admin/update-category-metadata-field-values")
    public ResponseEntity<Object> updateCategoryMetadataValues(
            @Valid @RequestBody CategoryMetadataFieldValuesDto
                    categoryMetadataFieldValuesDto){

        return adminService.updateCategoryMetadataValues(categoryMetadataFieldValuesDto);
    }

    /* <---- PRODUCT APIS -----> */

    @ApiOperation(value = "View a available product")
    @GetMapping("/admin/get-product")
    public MappingJacksonValue viewProduct(@RequestParam int productId) {

        ProductViewDto productViewDto = adminService.getProduct(productId);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productViewDto);
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

    @ApiOperation(value = "View a list of available products")
    @GetMapping("/admin/get-product-list")
    public MappingJacksonValue viewProductList() {
        List<ProductViewDto> productViewDtoList =  adminService.getProductList();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productViewDtoList);
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

    @ApiOperation(value = "Activate product with ID")
    @PutMapping("/admin/activate-product")
    public ResponseEntity<String> activateProduct(@RequestParam int productId) {
        return adminService.activateProduct(productId);
    }

    @ApiOperation(value = "Deactivate product with ID")
    @PutMapping("/admin/deactivate-product")
    public ResponseEntity<String> deActivateProduct(@RequestParam int productId) {
        return adminService.deactivateProduct(productId);
    }

    /* <---- ORDER APIS -----> */

    @ApiOperation(value = " View all Order List")
    @GetMapping("/admin/get-order-list/{pageSize}")
    public MappingJacksonValue getOrderList(Principal user, @PathVariable int pageSize){

        List<OrderViewDto> orderViewDtoList = adminService.getOrderList(user.getName(),pageSize);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(orderViewDtoList);
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

    @ApiOperation(value = " Change Order state of an ordered product ")
    @PutMapping("/admin/change-order-state")
    public ResponseEntity changeOrderState( Principal user,
                                       @Valid @RequestBody OrderStateChangeDto orderStateChangeDto){

        return adminService.changeOrderState(user.getName(),orderStateChangeDto);
    }




}
