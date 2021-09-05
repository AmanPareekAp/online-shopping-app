package com.online_shopping.service.user;

import com.online_shopping.dto.*;
import com.online_shopping.entity.order.*;
import com.online_shopping.entity.order.statusStates.FromStatusStates;
import com.online_shopping.entity.order.statusStates.ToStatusStates;
import com.online_shopping.entity.product.Category;
import com.online_shopping.entity.product.CategoryMetadataFieldValues;
import com.online_shopping.entity.product.Product;
import com.online_shopping.entity.product.ProductVariation;
import com.online_shopping.entity.user.*;
import com.online_shopping.exception.*;
import com.online_shopping.repository.*;
import com.online_shopping.service.Dto.CustomerFilterDto;
import com.online_shopping.service.product.ProductService;
import com.online_shopping.token.ConfirmationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {


    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private CustomerRepository customerRepository;
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private TokenRepository tokenRepository;
    private AddressRepository addressRepository;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private ProductVariationRepository productVariationRepository;
    private CartRepository cartRepository;
    private ProductService productService;
    private OrderRepository orderRepository;
    private OrderProductRepository orderProductRepository;
    private OrderStatusRepository orderStatusRepository;


    public CustomerService(CustomerRepository customerRepository,
                           UserService userService,
                           PasswordEncoder passwordEncoder,
                           TokenRepository tokenRepository,
                           AddressRepository addressRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           ProductVariationRepository productVariationRepository,
                           CartRepository cartRepository,
                           ProductService productService,
                           OrderRepository orderRepository,
                           OrderProductRepository orderProductRepository,
                           OrderStatusRepository orderStatusRepository) {

        this.customerRepository = customerRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.addressRepository = addressRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productVariationRepository=productVariationRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    //updating customer details by id
    public Customer updateCustomer(Customer customer){

        return customerRepository.save(customer);
    }

    //deleting an customer by id
    public User removeCustomer(int id){

        Customer customer=customerRepository.findById(id).get();
        customerRepository.delete(customer);

        return customer;

    }

    //getting All customers
    public List<Customer> getAllCustomers() throws Exception {

        return (List<Customer>) customerRepository.findAll();
    }

    public boolean checkIfUserExist(String email) {
        return customerRepository.findByEmail(email) != null;
    }


    public void register(CustomerDto customerDto) {

        Customer customer = new Customer();

        if (checkIfUserExist(customerDto.getEmail())) {
            throw new EmailExistsException("Already registered Email");
        }

        if(!(customerDto.getPassword().equals(customerDto.getConfirmPassword())))
        {
            throw new ConstraintViolationException(
                    "Enter same password in both password and confirm password field",
                    new HashSet<>());
        }

        customer.setEmail(customerDto.getEmail());
        customer.setFirstName(customerDto.getFirstName());

        if(customerDto.getMiddleName()!=null)
        {
            customer.setMiddleName(customerDto.getMiddleName());
        }

        if(customerDto.getLastName()!=null)
        {
            customer.setLastName(customerDto.getLastName());
        }

        customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));

        List<Long> contactList = new ArrayList<>();
        Pattern pattern = Pattern.compile("^\\d{10}$");


        for(Long contact : customerDto.getContactList())
        {
            Matcher matcher = pattern.matcher(contact.toString());
            if(!matcher.matches())
            {
                throw new RuntimeException("Contact Entered is invalid");
            }
            contactList.add(contact);
        }

        customer.setContactList(contactList);

        customer.setAddressList(customerDto.getAddressList());

        for (Address address : customer.getAddressList()) {
            address.setCustomer(customer);
        }

        Role role =userService.getRole(UserRole.CUSTOMER);
        customer.setRoleList(Arrays.asList(role));

        System.out.println(customer);
        customerRepository.save(customer);
        userService.sendActivationLinkToCustomer(customer);
    }

    public String confirmCustomerRegistration(String token) {
        System.out.println(token);
        ConfirmationToken confirmationToken= tokenRepository.findByToken(token);
        if(confirmationToken.getUser().isActive())
        {
            return "User is already active";
        }
        String result = userService.verifyToken(token);
        if (result.equals("invalidToken")) {
            throw new InvalidTokenException("Invalid Token");
        }
        if (result.equals("expired")) {
            throw new InvalidTokenException("Token expired");
        }

        Customer customer=(Customer) confirmationToken.getUser();
        System.out.println(customer);
        customer.setActive(true);
        customerRepository.save(customer);
        tokenRepository.delete(confirmationToken);

        return "Account Verified Successfully";

    }

    public ResponseEntity<String> resendToken(EmailDto emailDto) {
        Customer customer = customerRepository.findByEmail(emailDto.getEmail());
        if (customer == null) {
            throw new UserNotFoundException("No such email found!");
        }
        if (customer.isActive()) {
            userService.sendResetPasswordMessage(customer);
        } else {
            ConfirmationToken oldToken = tokenRepository.findTokenByUserId(customer.getId());
            tokenRepository.delete(oldToken);
            userService.sendActivationLinkToCustomer(customer);
        }

        return new ResponseEntity<>("Activation link has been resent.", HttpStatus.OK);
    }

    public CustomerViewDto viewProfile(String email) throws Exception {

        System.out.println(email);
        Customer customer = customerRepository.findByEmail(email);

        if(customer==null)
        {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        CustomerViewDto customerViewDto=new CustomerViewDto(customer);

        return customerViewDto;

    }

    public List<AddressViewDto> viewAddresses(String email) throws Exception {

        Customer customer = customerRepository.findByEmail(email);

        if(customer==null)
        {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        List<AddressViewDto> addressViewDtoList = new ArrayList<>();

        for(Address address: customer.getAddressList())
        {
            if(!address.isDeleted())
            addressViewDtoList.add(new AddressViewDto(address));
        }
        return addressViewDtoList;
    }

    public ResponseEntity<Object> updateProfile(String email,Customer newCustomer) {

        Customer customer=customerRepository.findByEmail(email);

        if(customer==null)
        {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        if(newCustomer==null)
        {
            throw new ConstraintViolationException("Enter details to be updated",
                    new HashSet<>());
        }

        if(newCustomer.getEmail()!=null)
        {
            customer.setEmail(newCustomer.getEmail());
        }
        if(newCustomer.getFirstName()!=null)
        {
            customer.setFirstName(newCustomer.getFirstName());
        }
        if(newCustomer.getMiddleName()!=null)
        {
            customer.setMiddleName(newCustomer.getMiddleName());
        }
        if(newCustomer.getLastName()!=null)
        {
            customer.setLastName(newCustomer.getLastName());
        }

        List<Long> contactList=new ArrayList<>();

        if(newCustomer.getContactList()!=null)
        {
            for(Long contact: newCustomer.getContactList())
            {
                contactList.add(contact);
            }

            customer.setContactList(contactList);
        }

        customerRepository.save(customer);

        return new ResponseEntity<>("Your profile has been updated",
                HttpStatus.OK);
    }


    public ResponseEntity<Object> updatePassword(String email, PasswordDto passwordDto) {

        Customer customer=customerRepository.findByEmail(email);

        if(customer==null)
        {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }


        if(passwordDto.getPassword()==null)
        {
            throw new ConstraintViolationException("Password cannot be empty",new HashSet<>());
        }

        if(!passwordDto.isSame())
        {
            throw new ConstraintViolationException("Password and Confirm Password should be same"
                                                    ,new HashSet<>());
        }

        customer.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        customerRepository.save(customer);
        return new ResponseEntity("Your password has been updated",HttpStatus.OK);
    }

    public ResponseEntity<Object> addAddresses(String email, Address address) {

        Customer customer = customerRepository.findByEmail(email);

        if(customer==null)
        {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        List<Address> addressList=customer.getAddressList();
        addressList.add(address);
        address.setCustomer(customer);
        customer.setAddressList(addressList);

        customerRepository.save(customer);

        return new ResponseEntity("Your address has been updated",HttpStatus.OK);

    }

    public ResponseEntity<Object> deleteAddresses(String email, int addressId) {

        Customer customer = customerRepository.findByEmail(email);

        if(customer==null)
        {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        Address address = addressRepository.findById(addressId).get();

        if(address.getCustomer().getId() != customer.getId())
        {
            throw new AddressNotFoundException("Enter valid address id");
        }
        address.setDeleted(true);

        for(Address tempAddress : customer.getAddressList() )
        {
            if(address.getId() == tempAddress.getId()) {
                customer.getAddressList().remove(tempAddress);
                break;
            }
        }
        customer.getAddressList().add(address);
        customerRepository.save(customer);

        return new ResponseEntity("Your address " + address.toString() +
                " has been deleted",
                HttpStatus.OK);

    }

    public ResponseEntity<Object> updateAddresses(String email, int addressId,
                                                  Address newAddress) {

        Customer customer = customerRepository.findByEmail(email);
//        System.out.println(email);

        if(customer==null)
        {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        if(newAddress==null)
        {
            throw new ConstraintViolationException("Address cannot be empty",new HashSet<>());
        }

        Address oldAddress = addressRepository.findById(addressId).get();

        if(oldAddress.getCustomer().getId() != customer.getId())
        {
            throw new AddressNotFoundException("Enter valid address id");
        }

        if(oldAddress.isDeleted())
        {
            throw new AddressNotFoundException("Address is deleted");
        }

        if(newAddress.getAddressLine()!=null)
        {
            oldAddress.setAddressLine(newAddress.getAddressLine());
        }

        if(newAddress.getCity()!=null)
        {
            oldAddress.setCity(newAddress.getCity());
        }

        if(newAddress.getState()!=null)
        {
            oldAddress.setState(newAddress.getState());
        }

        if(newAddress.getCountry()!=null)
        {
            oldAddress.setCountry(newAddress.getCountry());
        }

        if(newAddress.getLabel()!=null)
        {
            oldAddress.setLabel(newAddress.getLabel());
        }

        //check this
        if(newAddress.getZipCode()!=0)
        {
            oldAddress.setZipCode(newAddress.getZipCode());
        }

        customer.getAddressList().add(oldAddress);
        customerRepository.save(customer);
        return new ResponseEntity("Your address old address has been updated to: "
                + oldAddress.toString(),
                HttpStatus.OK);
    }

    public Iterable<CategoryViewDto> getCategoryList(int categoryId) {

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        Category category = optionalCategory.get();
        if(category==null)
        {
            throw new CategoryException("Category with id " + category +" does not exist");
        }

        Category parentCategory = category.getParentCategory();
        if(parentCategory == null) {

            return Arrays.asList(new CategoryViewDto(category));
        }

        List<Category> childCategoryList = parentCategory.getChildCategoryList();
        List<CategoryViewDto> categoryViewDtoList = new ArrayList<>();

        for(Category category1 : parentCategory.getChildCategoryList() )
        {
            categoryViewDtoList.add(new CategoryViewDto(category1));

        }

        return categoryViewDtoList;
    }

    //product apis

    public ProductViewDto getProduct(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent())
            throw new ProductException("Product with Id " + id + " not found.");
        Product product = optionalProduct.get();
        if (product.isActive() == false)
            throw new ProductException("Product has not been activated yet.");

        if(product.isDeleted())
            throw new ProductException("Product has been deleted");

        return new ProductViewDto(product);
    }

    public List<ProductViewDto> getProductList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductViewDto> productViewDtoList = new ArrayList<>();

        for(Product product : productPage)
        {
            if (product.isActive())
            productViewDtoList.add(new ProductViewDto(product));

        }

        return productViewDtoList;
    }

    public List<ProductViewDto> getSimilarProductList(int id) {
        List<Product> productList = productRepository.findAll();
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent())
            throw new ProductException("Product with Id " + id + " not found.");
        Product product = optionalProduct.get();
        List<ProductViewDto> productViewDtoList = new ArrayList<>();
        for (Product tempProduct : productList) {
            if (tempProduct.isActive() && (tempProduct.getCategory() == product.getCategory())) {
                productViewDtoList.add(new ProductViewDto(tempProduct));
            }
        }
        return productViewDtoList;
    }

    public CustomerFilterDto getCustomerFilters(int categoryId) {

        Category category = categoryRepository.findById(categoryId).get();

        if(category == null )
            throw new CategoryException("Category does not exist");

        List<Product> productList = category.getProductList();
        List<String> brandList = new ArrayList<>();
        Optional<ProductVariation> optionalMinPriceProductVariation = productVariationRepository.findAll()
                .stream().min(new Comparator<ProductVariation>() {
            @Override
            public int compare(ProductVariation productVariation, ProductVariation t1) {
                return productVariation.getPrice() - t1.getPrice();
            }
        });
        Optional<ProductVariation> optionalMaxPriceProductVariation = productVariationRepository.findAll()
                .stream().max(new Comparator<ProductVariation>() {
                    @Override
                    public int compare(ProductVariation productVariation, ProductVariation t1) {
                        return productVariation.getPrice() - t1.getPrice();
                    }
                });
        int minPrice=0, maxPrice=0;
        if(optionalMaxPriceProductVariation.isPresent() && optionalMinPriceProductVariation.isPresent())
        {
            minPrice = optionalMinPriceProductVariation.get().getPrice();
            maxPrice = optionalMaxPriceProductVariation.get().getPrice();
        }
        else
        {
            throw new RuntimeException("No Products available right now");
        }

        List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList =
                category.getCategoryMetadataFieldValuesList();

        for(Product product : productList)
        {
            brandList.add(product.getBrand());
        }

        List<CategoryMetadataFieldValuesViewDto> categoryMetadataFieldValuesViewDtoList =
                new ArrayList<>();

        for (CategoryMetadataFieldValues categoryMetadataFieldValues : categoryMetadataFieldValuesList)
        {
            categoryMetadataFieldValuesViewDtoList.add(
                    new CategoryMetadataFieldValuesViewDto(
                            categoryMetadataFieldValues.getMetadataValues()));
        }

        return new CustomerFilterDto(categoryMetadataFieldValuesViewDtoList,brandList,minPrice,maxPrice);

    }

    public ResponseEntity addProductToCart(String email, CartDto cartDto) {
        Customer customer = customerRepository.findByEmail(email);
//        System.out.println(email);
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        Optional<ProductVariation> optionalProductVariation = productVariationRepository
                .findById(cartDto.getProductVariationId());
        if (!optionalProductVariation.isPresent())
            throw new ProductException("Product Variation does not exist");

        ProductVariation productVariation = optionalProductVariation.get();

        if(!productService.checkProductStatus(productVariation.getProduct())){

            throw new ProductException("Product with id:" + productVariation.getProduct().getId() +
                " is either inactive or deleted");
        }

        if(!productVariation.isActive()){

            throw new ProductException("Product Variation with id:" + productVariation.getId() +
                    "is inactive");
        }


        CustomerProductVariationKey customerProductVariationKey =
                new CustomerProductVariationKey(customer.getId(), productVariation.getId());

        Cart cart = new Cart();

        if(cartRepository.findById(customerProductVariationKey).isPresent())
        {
            cart = cartRepository.findById(customerProductVariationKey).get();
            if(productVariation.getQuantityAvailable()==0)
            {
                throw new RuntimeException("Product Variation out of stock");
            }
            if(productVariation.getQuantityAvailable()>= cartDto.getQuantity()) {

                cart.setQuantity(cartDto.getQuantity() + cartDto.getQuantity());
            }
            else
            {
                throw new RuntimeException("Quantity entered for the product variation with id: "
                        + productVariation.getId() +
                        " is more than the quantity available, try lesser quantity");
            }

            cartRepository.save(cart);
            return ResponseEntity.ok("Your product has been added to cart with added quantity");

        }

        cart.setCustomer(customer);
        cart.setProductVariation(productVariation);
        cart.setCustomerProductVariationKey(customerProductVariationKey);

        if(productVariation.getQuantityAvailable()==0)
        {
            throw new RuntimeException("Product Variation out of stock");
        }
        if(productVariation.getQuantityAvailable()>= cartDto.getQuantity()) {

            cart.setQuantity(cartDto.getQuantity());
        }
        else
        {
            throw new RuntimeException("Quantity entered for the product variation with id: "
                    + productVariation.getId() +
                    " is more than the quantity available, try lesser quantity");
        }

        if(cartDto.isWishlistItem()!=cart.isWishlistItem())
            cart.setWishlistItem(cartDto.isWishlistItem());

        cartRepository.save(cart);

        return ResponseEntity.ok("Your product has been added to cart");
    }


    public List<CartViewDto> getCart(String email) {

        Customer customer = customerRepository.findByEmail(email);
        /*System.out.println(email);*/
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        List<Cart> cartList = cartRepository.findByCustomer(customer);
        List<CartViewDto> cartViewDtoList = new ArrayList<>();

        for(Cart cart : cartList ) {

            ProductVariation productVariation = cart.getProductVariation();
            if (productService.checkProductStatus(productVariation.getProduct())
                    && productVariation.isActive()) {

                cartViewDtoList.add(new CartViewDto(cart));
            }
        }
        return cartViewDtoList;
    }

    public ResponseEntity deleteProductFromCart(String email, int productVariationId) {

        Customer customer = customerRepository.findByEmail(email);
        /* System.out.println(email); */
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        Optional<ProductVariation> optionalProductVariation = productVariationRepository
                .findById(productVariationId);

        ProductVariation productVariation = optionalProductVariation.get();

        if(!productService.checkProductStatus(productVariation.getProduct())){

            throw new ProductException("Product with id:" + productVariation.getProduct().getId() +
                    " is either inactive or already deleted");
        }

        if(!productVariation.isActive()){

            throw new ProductException("Product Variation with id:" + productVariation.getId() +
                    "is inactive");
        }

        Optional<Cart> optionalCart = cartRepository.findById(new CustomerProductVariationKey(
                customer.getId(),productVariationId));

        Cart cart = optionalCart.get();

        cartRepository.deleteById(cart.getCustomerProductVariationKey());

        return ResponseEntity.ok("Your product is deleted from cart");
    }

    public ResponseEntity<Object> updateProductInCart(String email,
                                                      ProductVariationUpdateDto productVariationUpdateDto) {
        Customer customer = customerRepository.findByEmail(email);
//        System.out.println(email);
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        Optional<ProductVariation> optionalProductVariation = productVariationRepository
                .findById(productVariationUpdateDto.getId());
        if (!optionalProductVariation.isPresent())
            throw new ProductException("Product Variation does not exist");

        ProductVariation productVariation = optionalProductVariation.get();

        if(!productService.checkProductStatus(productVariation.getProduct())){

            throw new ProductException("Product with id:" + productVariation.getProduct().getId() +
                    " is either inactive or deleted");
        }

        if(!productVariation.isActive()){

            throw new ProductException("Product Variation with id:" + productVariation.getId() +
                    "is inactive");
        }

        CustomerProductVariationKey customerProductVariationKey =
                new CustomerProductVariationKey(customer.getId(), productVariation.getId());

        Optional<Cart> optionalCart = cartRepository.findById(customerProductVariationKey);
        Cart cart = optionalCart.get();

        if(productVariationUpdateDto.getQuantityAvailable()==0)
        {
            cartRepository.delete(cart);
            return ResponseEntity.ok("Your cart is updated");
        }

        if(productVariation.getQuantityAvailable()==0)
        {
            throw new RuntimeException("Product Variation out of stock");
        }

        if(productVariation.getQuantityAvailable()>= productVariationUpdateDto.getQuantityAvailable()) {

            cart.setQuantity(productVariationUpdateDto.getQuantityAvailable());
        }
        else
        {
            throw new RuntimeException("Quantity entered for the product variation with id: "
                    + productVariation.getId() +
                    " is more than the quantity available, try lesser quantity");
        }
        cartRepository.save(cart);

        return ResponseEntity.ok("Your cart is updated");
    }

    public ResponseEntity deleteCart(String email) {

        Customer customer = customerRepository.findByEmail(email);
        /*System.out.println(email);*/
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        List<Cart> cartList = cartRepository.findByCustomer(customer);

        cartRepository.deleteAll(cartList);

        return ResponseEntity.ok("Your cart is empty");
    }

    public ResponseEntity orderAllProductsFromCart(String email, OrderProductsDto orderProductsDto) {

        Customer customer = customerRepository.findByEmail(email);
//        System.out.println(email);
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        List<Cart> cartList = cartRepository.findByCustomer(customer);

        Optional<Address> optionalAddress = addressRepository.findById(orderProductsDto.getAddressId());
        Address address = optionalAddress.get();
        Order order = new Order(new Date(), orderProductsDto.getPaymentMethod(),address);
        order.setCustomer(customer);
        customer.getOrderList().add(order);

        ProductVariation productVariation = new ProductVariation();
        OrderProduct orderProduct = new OrderProduct();
        OrderStatus orderStatus = new OrderStatus();

        List<OrderProduct> orderProductList = new ArrayList<>();
        List<OrderStatus> orderStatusList = new ArrayList<>();

        for(Cart cart : cartList)
        {
            productVariation = cart.getProductVariation();
            if(productService.checkProductStatus(productVariation.getProduct())
                    && productVariation.isActive()) {

                orderProduct = new OrderProduct();

                if(productVariation.getQuantityAvailable() != 0)
                {
                    if(cart.getQuantity()<=productVariation.getQuantityAvailable()) {

                        int quantityLeft = productVariation.getQuantityAvailable()- cart.getQuantity();
                        productVariation.setQuantityAvailable(quantityLeft);
                        orderProduct.setQuantity(cart.getQuantity());
                    }
                    else
                        throw new RuntimeException("Cannot place order as Product Variation with id : "
                        + productVariation.getId() + " is less than the added quantity");
                }
                else
                {
                    throw new RuntimeException("Product Variation out of stock");
                }

                orderProduct.setPrice(productVariation.getPrice() * orderProduct.getQuantity());
                order.setAmount(order.getAmount() + orderProduct.getPrice());
                orderProduct.setProductVariationMetadata(productVariation.getMetadataString());
                orderProduct.setProductVariation(productVariation);
                orderProduct.setOrder(order);
                orderProductList.add(orderProduct);

                orderStatus = new OrderStatus();
                orderStatus.setOrderProduct(orderProduct);
                orderProduct.setOrderStatus(orderStatus);
                orderStatus.setTransitionNotesComments("Order Placed Successfully");
                orderStatus.setFromStatus(FromStatusStates.ORDER_PLACED.name());
                orderStatus.setToStatus(ToStatusStates.ORDER_CONFIRMED.name());
                orderStatusList.add(orderStatus);
            }
            else{
                throw new RuntimeException("Product is inactive or deleted");
            }
        }
        order.setOrderProductList(orderProductList);
        int orderId = orderRepository.save(order).getId();
        logger.info("After Order saved");
//        orderProductRepository.save(orderProduct);
//        logger.info("After Order Product saved");
//        orderStatusRepository.save(orderStatus);
        logger.info("After All saved");
        return ResponseEntity.ok("Order has been placed, your order id is : " + orderId);
    }

    public ResponseEntity orderSomeProductsFromCart(String email, PartialOrderDto partialOrderDto) {

        Customer customer = customerRepository.findByEmail(email);
        Optional<Address> optionalAddress = addressRepository.findById(partialOrderDto
                .getOrderProductsDto().getAddressId());
        Address address = optionalAddress.get();
        Order order = new Order(new Date(),partialOrderDto.getOrderProductsDto()
                .getPaymentMethod(),address);
        order.setCustomer(customer);
        customer.getOrderList().add(order);

        List<Cart> cartList = new ArrayList<>();

        for(int productVariationId : partialOrderDto.getProductVariationIdList())
        {
            Optional<Cart> optionalCart = cartRepository.findById(new CustomerProductVariationKey(customer.getId(),
                    productVariationId));
            Cart cart = optionalCart.get();
            cartList.add(cart);
        }

        ProductVariation productVariation = new ProductVariation();
        OrderProduct orderProduct = new OrderProduct();
        OrderStatus orderStatus = new OrderStatus();

        List<OrderProduct> orderProductList = new ArrayList<>();
        List<OrderStatus> orderStatusList = new ArrayList<>();

        for(Cart cart : cartList)
        {
            productVariation = cart.getProductVariation();
            if(productService.checkProductStatus(productVariation.getProduct())
                    && productVariation.isActive()) {

                orderProduct = new OrderProduct();

                if(productVariation.getQuantityAvailable() != 0)
                {
                    if(cart.getQuantity()<=productVariation.getQuantityAvailable()) {

                        int quantityLeft = productVariation.getQuantityAvailable()- cart.getQuantity();
                        productVariation.setQuantityAvailable(quantityLeft);
                        orderProduct.setQuantity(cart.getQuantity());
                    }
                    else
                        throw new RuntimeException("Cannot place order as Product Variation with id : "
                                + productVariation.getId() + " is less than the added quantity");
                }
                else
                {
                    throw new RuntimeException("Product Variation out of stock");
                }

                orderProduct.setPrice(productVariation.getPrice() * orderProduct.getQuantity());
                order.setAmount(order.getAmount() + orderProduct.getPrice());
                orderProduct.setProductVariationMetadata(productVariation.getMetadataString());
                orderProduct.setProductVariation(productVariation);
                orderProduct.setOrder(order);
                orderProductList.add(orderProduct);

                orderStatus = new OrderStatus();
                orderStatus.setOrderProduct(orderProduct);
                orderProduct.setOrderStatus(orderStatus);
                orderStatus.setTransitionNotesComments("Order Placed Successfully");
                orderStatus.setFromStatus(FromStatusStates.ORDER_PLACED.name());
                orderStatus.setToStatus(ToStatusStates.ORDER_CONFIRMED.name());
                orderStatusList.add(orderStatus);
            }
            else{
                throw new RuntimeException("Product is inactive or deleted");
            }
        }
        order.setOrderProductList(orderProductList);
        int orderId = orderRepository.save(order).getId();
        logger.info("After Order saved");
//        orderProductRepository.save(orderProduct);
//        logger.info("After Order Product saved");
//        orderStatusRepository.save(orderStatus);
        logger.info("After All saved");
        return ResponseEntity.ok("Order has been placed, your order id is : " + orderId);

    }

    public ResponseEntity orderOneProduct(String email, OrderProductsDto orderProductsDto,
                                          int productVariationId, int orderQuantity) {

        Customer customer = customerRepository.findByEmail(email);
        Optional<Address> optionalAddress = addressRepository.findById(orderProductsDto.getAddressId());
        Address address = optionalAddress.get();
        Order order = new Order(new Date(),orderProductsDto.getPaymentMethod(),address);
        order.setCustomer(customer);
        customer.getOrderList().add(order);

        OrderProduct orderProduct = new OrderProduct();
        OrderStatus orderStatus = new OrderStatus();
        Optional<ProductVariation> optionalProductVariation = productVariationRepository
                .findById(productVariationId);

        ProductVariation productVariation = optionalProductVariation.get();

        if(!productService.checkProductStatus(productVariation.getProduct()))
            throw new RuntimeException("Product not active or deleted");

        if (!productVariation.getProduct().isActive())
            throw new RuntimeException("Product is not Active");

        if(productVariation.getQuantityAvailable() == 0) {

            throw new RuntimeException("Product Variation with id : " + productVariation.getId() +
                    "out of stock");
        }

        if(orderQuantity<=productVariation.getQuantityAvailable()) {

                int quantityLeft = productVariation.getQuantityAvailable()- orderQuantity;
                productVariation.setQuantityAvailable(quantityLeft);
                orderProduct.setQuantity(orderQuantity);
        }
        else
            {
                throw new RuntimeException("Cannot place order as Product Variation with id : " +
                        productVariation.getId() + " is less than the added quantity");
        }

        int price = productVariation.getPrice() * orderQuantity;
        orderProduct.setProductVariation(productVariation);
        orderProduct.setProductVariationMetadata(productVariation.getMetadataString());
        orderProduct.setOrder(order);
        orderProduct.setQuantity(orderQuantity);
        orderProduct.setPrice(price);

        orderStatus.setOrderProduct(orderProduct);
        orderStatus.setTransitionNotesComments("Order Placed Successfully");
        orderStatus.setFromStatus(FromStatusStates.ORDER_PLACED.name());
        orderStatus.setToStatus(ToStatusStates.ORDER_CONFIRMED.name());
        orderProduct.setOrderStatus(orderStatus);

        order.setAmount(price);
        order.getOrderProductList().add(orderProduct);
        int orderId = orderRepository.save(order).getId();
//        logger.info("After Order saved");
        return ResponseEntity.ok("Order has been placed, your order id is : " + orderId);
    }

    public ResponseEntity cancelOrder(String email, int orderProductId) {

        Customer customer = customerRepository.findByEmail(email);
        Optional<OrderProduct> optionalOrderProduct = orderProductRepository.findById(orderProductId);
        OrderProduct orderProduct = optionalOrderProduct.get();

        Optional<OrderStatus> optionalOrderStatus = orderStatusRepository.findById(orderProductId);
        OrderStatus orderStatus = optionalOrderStatus.get();

        if (!orderProduct.getOrderStatus().getFromStatus().equals(FromStatusStates.ORDER_PLACED.name()))
            throw new RuntimeException("This Order is not available or not placed yet.");

        Optional<Order> optionalOrder = orderRepository.findById(orderProduct.getOrder().getId());

        Order order = optionalOrder.get();
        if (order.getCustomer().getId() != customer.getId())
            throw new RuntimeException("This Order does not belong to you");

        orderStatus.setTransitionNotesComments(" Order Cancelled ");
        orderStatus.setToStatus(ToStatusStates.CANCELLED.name());
        orderProduct.setOrderStatus(orderStatus);
        orderProductRepository.save(orderProduct);
        return ResponseEntity.ok("Your order cancelled successfully");
    }

    public ResponseEntity returnOrder(String email, int orderProductId) {
        Customer customer = customerRepository.findByEmail(email);
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        Optional<OrderProduct> optionalOrderProduct = orderProductRepository.findById(orderProductId);

        OrderProduct orderProduct = optionalOrderProduct.get();

        Optional<OrderStatus> optionalOrderStatus = orderStatusRepository.findById(orderProductId);
        OrderStatus orderStatus = optionalOrderStatus.get();

        if (!orderProduct.getOrderStatus().getFromStatus().equals(FromStatusStates.DELIVERED.name()))
            throw new RuntimeException("This Order is not available or not Delivered yet.");

        Optional<Order> order = orderRepository.findById(orderProduct.getOrder().getId());
        Order order1 = order.get();
        if (order1.getCustomer().getId() != customer.getId())
            throw new RuntimeException("This order does not belong to you");

        orderStatus.setTransitionNotesComments(" Order Return Request ");
        orderStatus.setToStatus(ToStatusStates.RETURN_REQUESTED.name());
        orderProduct.setOrderStatus(orderStatus);
        orderProductRepository.save(orderProduct);
        return ResponseEntity.ok("Your order returned successfully");
    }

    public OrderViewDto getOrder(String email, int orderId) {

        Customer customer = customerRepository.findByEmail(email);
//        System.out.println(email);
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.get();

        List<Order> customerOrderList = customer.getOrderList();

        boolean ifOrderIsCustomerOrder = false;

        for(Order order1 : customerOrderList)
        {
            if(order1.getId() == order.getId()) {
                ifOrderIsCustomerOrder = true;
                break;
            }
        }

        if(!ifOrderIsCustomerOrder)
        {
            throw new RuntimeException("Customer does not have with order id : " + orderId);
        }
        return new OrderViewDto(order);

    }

    public Page<OrderViewDto> getOrderList(String email, int pageSize) {
        Customer customer = customerRepository.findByEmail(email);
//      System.out.println(email);
        if(customer==null)
            throw new UserNotFoundException("User Not Found, enter valid email");

        List<Order> orderList = customer.getOrderList();
        List<OrderViewDto> orderViewDtoList = new ArrayList<>();
        for (Order order : orderList)
        {
            orderViewDtoList.add(new OrderViewDto(order));
        }

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id"));
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), orderViewDtoList.size());
        final Page<OrderViewDto> orderViewDtoPage = new PageImpl<OrderViewDto>(
                orderViewDtoList.subList(start, end), pageable, orderViewDtoList.size());
        return orderViewDtoPage;
    }
}
