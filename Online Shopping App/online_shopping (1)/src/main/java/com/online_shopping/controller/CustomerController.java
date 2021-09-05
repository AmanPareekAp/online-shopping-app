package com.online_shopping.controller;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.online_shopping.dto.*;
import com.online_shopping.entity.user.Address;
import com.online_shopping.entity.user.Customer;
import com.online_shopping.service.Dto.CustomerFilterDto;
import com.online_shopping.service.user.CustomerService;
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
@Api(value = "APIs Related to Customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    /* <---- CUSTOMER REGISTRATION APIS -----> */

    @ApiOperation(value = "Customer Registration")
    @PostMapping("/customer/register")
    public ResponseEntity<Object> registerCustomer(@Valid @RequestBody
                                                   CustomerDto customerDto,
                                                   HttpServletRequest request) {

        customerService.register(customerDto);
        return new ResponseEntity<>("Your account created successfully," +
                " please check you email for account activation", HttpStatus.OK);
    }


    @ApiOperation(value = "Customer Confirm Registration")
    @PutMapping("/customer/confirm-account")
    public ResponseEntity<Object> confirmRegistration(@RequestParam("token") String token) {
        String responseMessage = customerService.confirmCustomerRegistration(token);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ApiOperation(value = "Customer Resend Registration Token")
    @PostMapping("/register/customer/resend-token")
    public ResponseEntity<String> resendRegistrationToken(@Valid @RequestBody EmailDto emailDto) {
       return  customerService.resendToken(emailDto);

    }

    /* <---- CUSTOMER APIS -----> */

    @ApiOperation(value = "View a customer profile")
    @GetMapping("/customer/view-profile")
    public CustomerViewDto viewProfile(Principal user)
                                       throws Exception {
        return customerService.viewProfile(user.getName());
    }

    @ApiOperation(value = "View a customer address")
    @GetMapping("/customer/view-addresses")
    public List<AddressViewDto> viewAddresses(Principal user)
                                                throws Exception {
        return customerService.viewAddresses(user.getName());
    }

    @ApiOperation(value = "Update a customer profile")
    @PatchMapping("/customer/update-profile")
    public ResponseEntity<Object> updateProfile(Principal user,
                                                @RequestBody Customer customer) throws Exception {

        return customerService.updateProfile(user.getName(), customer);
    }

    @ApiOperation(value = "Update customer password")
    @PatchMapping("/customer/update-password")
    public ResponseEntity<Object> updatePassword(Principal user,
                                                 @RequestBody PasswordDto passwordDto)
                                                 throws Exception {

        return customerService.updatePassword(user.getName(), passwordDto);
    }

    @ApiOperation(value = "Add a customer address")
    @PostMapping("/customer/add-address")
    public ResponseEntity addAddress(Principal user,
                                     @RequestBody Address address){

        return customerService.addAddresses(user.getName(), address);
    }

    @ApiOperation(value = "Delete a customer address")
    @DeleteMapping("/customer/delete-address")
    public ResponseEntity<Object> deleteAddress(Principal user,
                                        @RequestParam int addressId){

        return customerService.deleteAddresses(user.getName(), addressId);
    }


    @ApiOperation(value = "Update a customer address")
    @PatchMapping("/customer/update-address")
    public ResponseEntity<Object> updateAddress(Principal user,
                                                @RequestParam int addressId,
                                                @RequestBody Address address){

        return customerService.updateAddresses(user.getName(), addressId, address);
    }

    /* <---- CATEGORY APIS -----> */

    @ApiOperation(value = "View a list of available Category")
    @GetMapping("/customer/get-category-list")
    public Iterable<CategoryViewDto> getCategories(@RequestParam int categoryId) {
        Iterable<CategoryViewDto> categoryViewDtoIterable = customerService.getCategoryList(categoryId);

        return categoryViewDtoIterable;
    }

    @ApiOperation(value = "View a Filter list for products")
    @GetMapping("/customer/get-category-filter-list")
    public CustomerFilterDto getCustomerFilters(@RequestParam int categoryId)
    {
        return customerService.getCustomerFilters(categoryId);
    }

    /* <---- PRODUCT APIS -----> */

    @ApiOperation(value = "View a available product")
    @GetMapping("/customer/get-product")
    public MappingJacksonValue viewProduct(@RequestParam int id) {

        ProductViewDto productViewDto = customerService.getProduct(id);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productViewDto);
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "brand",
                "category","isCancellable", "isReturnable");
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("ProductViewFilter", filter1);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }


    @ApiOperation(value = "View a available product list")
    @GetMapping("/customer/get-product-list")
    public MappingJacksonValue viewAllProduct() {

        List<ProductViewDto> productViewDtoList = customerService.getProductList();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productViewDtoList);
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "brand",
                "category","isCancellable", "isReturnable");
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("ProductViewFilter", filter1);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    @ApiOperation(value = "View a available similar product list")
    @GetMapping("/customer/get-similar-product-list")
    public MappingJacksonValue viewSimilarProduct(@RequestParam int id) {

        List<ProductViewDto> productViewDtoList = customerService.getSimilarProductList(id);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(productViewDtoList);
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("id","name", "brand",
                "category","isCancellable", "isReturnable");
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("ProductViewFilter", filter1);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;

    }

    /* <---- CART APIS -----> */

    @ApiOperation(value = "Add a product to cart")
    @PostMapping("/customer/add-product-to-cart")
    public ResponseEntity addProductToCart( Principal user ,@Valid @RequestBody CartDto cartDto){

        return customerService.addProductToCart(user.getName(),cartDto);
    }

    @ApiOperation(value = "View a list products in the cart")
    @GetMapping("/customer/get-cart")
    public MappingJacksonValue getCart(Principal user)
    {
        List<CartViewDto> cartViewDto = customerService.getCart(user.getName());
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(cartViewDto);
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

    @ApiOperation(value = "Delete product from the cart")
    @DeleteMapping("/customer/delete-product-from-cart")
    public ResponseEntity deleteProductFromCart(Principal user,@RequestParam int productVariationId)
    {
        return customerService.deleteProductFromCart(user.getName(),productVariationId);
    }

    @ApiOperation(value = "Update product in the cart")
    @PutMapping("/customer/update-product-in-cart")
    public ResponseEntity<Object> updateProductInCart(Principal user, @RequestBody
            ProductVariationUpdateDto productVariationUpdateDto){
        return customerService.updateProductInCart(user.getName(), productVariationUpdateDto);
    }

    @ApiOperation(value = "Empty the cart")
    @DeleteMapping("/customer/delete-cart")
    public ResponseEntity deleteCart(Principal user)
    {
        return customerService.deleteCart(user.getName());
    }

    /* <---- ORDER APIS -----> */

    @ApiOperation(value = "Order All Product from the Cart")
    @PostMapping("/customer/order-all-products-from-cart")
    public ResponseEntity orderAllProductsFromCart( Principal user,
                                                    @Valid @RequestBody OrderProductsDto orderProductsDto){

        return customerService.orderAllProductsFromCart(user.getName(), orderProductsDto);
    }

    @ApiOperation(value = "Order Partial Product from the Cart")
    @PostMapping("/customer/order-some-products-from-cart")
    public ResponseEntity orderSomeProductsFromCart( Principal user,
                                                     @Valid @RequestBody PartialOrderDto partialOrderDto ){

        return customerService.orderSomeProductsFromCart(user.getName(),partialOrderDto);
    }

    @ApiOperation(value = "Order Product Directly ")
    @PostMapping("/customer/order-one-product")
    public ResponseEntity orderOneProducts( Principal user,
                                          @Valid @RequestBody OrderProductsDto orderProductsDto,
                                            @RequestParam int productVariationId,
                                            @RequestParam int quantity ){

        return customerService.orderOneProduct(user.getName(),orderProductsDto, productVariationId,quantity);
    }

    @ApiOperation(value = " Cancel order ")
    @PutMapping("/customer/cancel-order")
    public ResponseEntity cancelOrder( Principal user, @RequestParam  int orderProductId ){

        return customerService.cancelOrder(user.getName(),orderProductId);
    }

    @ApiOperation(value = " Return order ")
    @PutMapping("/customer/return-order")
    public ResponseEntity returnOrder( Principal user,
                                       @RequestParam  int orderProductId ){

        return customerService.returnOrder(user.getName(),orderProductId);
    }

    @ApiOperation(value = " View an order ")
    @GetMapping("/customer/get-order")
    public MappingJacksonValue getOrder(Principal user, @RequestParam  int orderId ){

        OrderViewDto orderViewDto = customerService.getOrder(user.getName(),orderId);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(orderViewDto);
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

    @ApiOperation(value = " View customer's order list")
    @GetMapping("/customer/get-order-list/{pageSize}")
    public MappingJacksonValue getOrderList(Principal user, @PathVariable int pageSize){

        Page<OrderViewDto> orderViewDtoPage = customerService.getOrderList(user.getName(),pageSize);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(orderViewDtoPage.getContent());
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







}
