package com.online_shopping.service.user;

import com.online_shopping.dto.*;
import com.online_shopping.entity.order.OrderProduct;
import com.online_shopping.entity.order.statusStates.FromStatusStates;
import com.online_shopping.entity.order.statusStates.ToStatusStates;
import com.online_shopping.entity.product.Category;
import com.online_shopping.entity.product.Product;
import com.online_shopping.entity.product.ProductVariation;
import com.online_shopping.entity.user.Address;
import com.online_shopping.entity.user.Role;
import com.online_shopping.entity.user.Seller;
import com.online_shopping.entity.user.UserRole;
import com.online_shopping.exception.CategoryException;
import com.online_shopping.exception.EmailExistsException;
import com.online_shopping.exception.ProductException;
import com.online_shopping.exception.UserNotFoundException;
import com.online_shopping.repository.*;
import com.online_shopping.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SellerService {

    private static final Logger logger = LoggerFactory.getLogger(SellerService.class);

    SellerRepository sellerRepository;
    UserService userService;
    PasswordEncoder passwordEncoder;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;
    ProductVariationRepository productVariationRepository;
    ProductService productService;
    OrderRepository orderRepository;
    OrderProductRepository orderProductRepository;
    OrderStatusRepository orderStatusRepository;

    public SellerService(SellerRepository sellerRepository,
                         UserService userService,
                         PasswordEncoder passwordEncoder,
                         ProductRepository productRepository,
                         CategoryRepository categoryRepository,
                         CategoryMetadataFieldRepository categoryMetadataFieldRepository,
                         CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository,
                         ProductVariationRepository productVariationRepository,
                         ProductService productService,
                         OrderRepository orderRepository,
                         OrderStatusRepository orderStatusRepository,
                         OrderProductRepository orderProductRepository) {

        this.sellerRepository = sellerRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.categoryMetadataFieldRepository = categoryMetadataFieldRepository;
        this.categoryMetadataFieldValuesRepository = categoryMetadataFieldValuesRepository;
        this.productVariationRepository = productVariationRepository;
        this.productService = productService ;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderProductRepository = orderProductRepository;
    }

    public boolean checkIfUserExist(String email) {

        return sellerRepository.findByEmail(email) != null;
    }

    public void register(@Valid SellerDto sellerDto) {

        Seller seller = new Seller();

        if (checkIfUserExist(sellerDto.getEmail())) {
            throw new EmailExistsException("Already registered Email");
        }

        if (!(sellerDto.getPassword().equals(sellerDto.getConfirmPassword()))) {
            throw new ConstraintViolationException(
                    "Enter same password in both password and confirm password field",
                    new HashSet<>());
        }

        seller.setEmail(sellerDto.getEmail());
        seller.setFirstName(sellerDto.getFirstName());

        if (sellerDto.getMiddleName() != null) {
            seller.setMiddleName(sellerDto.getMiddleName());
        }

        if (sellerDto.getLastName() != null) {
            seller.setLastName(sellerDto.getLastName());
        }
        seller.setPassword(passwordEncoder.encode(sellerDto.getPassword()));

        List<Long> contactList = new ArrayList<>();
        Pattern pattern = Pattern.compile("^\\d{10}$");
        for(Long contact : sellerDto.getContactList())
        {
            Matcher matcher = pattern.matcher(contact.toString());
            if(!matcher.matches())
            {
                throw new RuntimeException("Contact Entered is invalid");
            }
            contactList.add(contact);
        }
        seller.setContactList(contactList);

        seller.setAddress(sellerDto.getAddress());

        Role role = userService.getRole(UserRole.SELLER);
        seller.setRoleList(Arrays.asList(role));

        seller.setGst(sellerDto.getGst());
        seller.setCompanyName(sellerDto.getCompanyName());

        seller.getAddress().setSeller(seller);
        sellerRepository.save(seller);
        userService.sendActivationLinkToSeller(seller);
    }

    public SellerViewDto viewProfile(String email) {

        //logger.info(email);
        Seller seller = sellerRepository.findByEmail(email);

        if (seller == null) {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        SellerViewDto sellerViewDto = new SellerViewDto(seller);

        return sellerViewDto;
    }

    public ResponseEntity<Object> updateProfile(String email, Seller newSeller) {

        Seller seller = sellerRepository.findByEmail(email);

        if (seller == null) {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        if (newSeller == null) {
            throw new ConstraintViolationException("Enter details to be updated",
                    new HashSet<>());
        }

        if (newSeller.getEmail() != null) {
            seller.setEmail(seller.getEmail());
        }
        if (newSeller.getFirstName() != null) {
            seller.setFirstName(newSeller.getFirstName());
        }
        if (newSeller.getMiddleName() != null) {
            seller.setMiddleName(newSeller.getMiddleName());
        }
        if (newSeller.getLastName() != null) {
            seller.setLastName(newSeller.getLastName());
        }

        if (newSeller.getGst() != null) {
            seller.setGst(newSeller.getGst());
        }

        if (newSeller.getCompanyName() != null) {
            seller.setCompanyName(newSeller.getCompanyName());
        }

        List<Long> contactList = new ArrayList<>();

        if (newSeller.getContactList() != null) {
            for (Long contact : newSeller.getContactList()) {
                contactList.add(contact);
            }

            seller.setContactList(contactList);
        }


        sellerRepository.save(seller);

        return new ResponseEntity<>("Your profile has been updated",
                HttpStatus.OK);
    }

    public ResponseEntity<Object> updatePassword(String email, PasswordDto passwordDto) {

        Seller seller = sellerRepository.findByEmail(email);

        if (seller == null) {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }


        if (passwordDto.getPassword() == null) {
            throw new ConstraintViolationException("Password cannot be empty", new HashSet<>());
        }

        if (!passwordDto.isSame()) {
            throw new ConstraintViolationException("Password and Confirm Password should be same"
                    , new HashSet<>());
        }

        seller.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        sellerRepository.save(seller);

        return new ResponseEntity("Your password has been updated", HttpStatus.OK);
    }


    public ResponseEntity<Object> updateAddresses(String email, Address newAddress) {

        Seller seller = sellerRepository.findByEmail(email);

        if (seller == null) {
            throw new UserNotFoundException("User Not Found, enter valid email");
        }

        if (newAddress == null) {
            throw new ConstraintViolationException("Enter valid address to update the address",
                    new HashSet<>());
        }

        Address oldAddress = seller.getAddress();

        if (newAddress.getAddressLine() != null) {
            oldAddress.setAddressLine(newAddress.getAddressLine());
        }

        if (newAddress.getCity() != null) {
            oldAddress.setCity(newAddress.getCity());
        }

        if (newAddress.getState() != null) {
            oldAddress.setState(newAddress.getState());
        }

        if (newAddress.getCountry() != null) {
            oldAddress.setCountry(newAddress.getCountry());
        }

        if (newAddress.getLabel() != null) {
            oldAddress.setLabel(newAddress.getLabel());
        }

        //check this
        if (newAddress.getZipCode() != 0) {
            oldAddress.setZipCode(newAddress.getZipCode());
        }

        seller.setAddress(oldAddress);

        sellerRepository.save(seller);

        return new ResponseEntity("Your address old address has been updated to: "
                + oldAddress.toString(),
                HttpStatus.OK);

    }

    public Iterable<CategoryViewDto> getCategoryList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Category> categoryPageList = categoryRepository.findAll(pageable);

        List<CategoryViewDto> categoryViewDtoList = new ArrayList<>();

        for (Category category : categoryPageList) {
            if (category.getChildCategoryList() == null || category.getChildCategoryList().size() == 0)
                categoryViewDtoList.add(new CategoryViewDto(category));
        }

        return categoryViewDtoList;
    }

    public ResponseEntity<Object> addProduct(String email, ProductDto productDto) {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());
        Category category = optionalCategory.get();
        if (category == null) {
            throw new CategoryException("Category Id not found.");
        }

        if(category.getChildCategoryList()!=null && category.getChildCategoryList().size()!=0)
        {
            throw new CategoryException("Only root categories can be used add products");
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        seller.setProductList(Arrays.asList(product));
        product.setCategory(category);
        product.setSellerList(Arrays.asList(seller));
        System.out.println("Before saving");
        productRepository.save(product);
        System.out.println("AFTER saving");
        userService.sendProductActivationReminderToAdmin(product);
        System.out.println("AFTER sending mail");

        return new ResponseEntity("Product has been added, waiting for activation", HttpStatus.OK);
    }

    public ProductViewDto viewProductById(String email, int id) {

        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }

        Optional<Product> optionalProduct = productRepository.findById(id);

        if (!optionalProduct.isPresent())
            throw new ProductException("Product with id " + id + " not found");


        if(!productService.checkOwner(seller,id))
        {
            throw new ProductException("You are not owner of the product," +
                    " so you cannot view the product. ");
        }

        Product product = optionalProduct.get();

        if(!productService.checkProductStatus(product))
        {
            throw new ProductException("Product with id:" + id + " is either inactive or deleted");
        }

        return new ProductViewDto(optionalProduct.get());
    }

    public List<ProductViewDto> viewAllProductDetails(String email) {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }
        List<Product> productList = seller.getProductList();
        List<ProductViewDto> productViewDtoList = new ArrayList<>();

        for (Product product : productList) {

            if(!productService.checkProductStatus(product))
            {
                throw new ProductException("Product with id:" + product.getId()
                        + " is either inactive or deleted");
            }

            productViewDtoList.add(new ProductViewDto(product));

        }

        return productViewDtoList;
    }


    public ResponseEntity<String> deleteProduct(int id, String email) {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent())
            throw new ProductException("Product with id " + id + " not found.");
        Product product = optionalProduct.get();

        if(!productService.checkOwner(seller,id))
        {
            throw new ProductException("You are not owner of the product," +
                    " so you cannot delete the product. ");
        }

        if(!productService.checkProductStatus(product))
        {
            throw new ProductException("Product with id:" + id + " is either inactive or deleted already");
        }

        List<Seller> sellerList = product.getSellerList();

        if(!productService.checkOwner(seller,id))
        {
            throw new ProductException("You are not owner of the product," +
                    " so you cannot view the product. ");
        }
        else {

            sellerList.remove(seller);
            if (sellerList.size() == 0) {
                product.setDeleted(true);
            }
            productRepository.save(product);
            seller.getProductList().remove(product);
            sellerRepository.save(seller);

            return new ResponseEntity<>("Product deleted successfully!", HttpStatus.OK);

        }

    }

    public ResponseEntity<String> updateProduct(ProductDto productDto, int id, String email) {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }
        Optional<Product> productOptional = productRepository.findById(id);
        Product product = productOptional.get();

        if(!productService.checkProductStatus(product))
        {
            throw new ProductException("Product with id:" + id + " is either inactive or deleted");
        }

        if(!productService.checkOwner(seller,id))
        {
            throw new ProductException("You are not owner of the product," +
                    " so you cannot update the product. ");
        }

        if(productDto.getName()!=null)
            product.setName(productDto.getName());

        if(productDto.getName()!=null)
            product.setBrand(productDto.getBrand());

        if(productDto.getDescription()!=null)
            product.setDescription(productDto.getDescription());

        if(productDto.isCancellable()!=product.isCancellable())
            product.setCancellable(productDto.isCancellable());

        if(productDto.isReturnable()!=product.isReturnable())
            product.setReturnable(productDto.isReturnable());

        productRepository.save(product);

        return new ResponseEntity<>("Product updated successfully!", HttpStatus.OK);
    }

    public ResponseEntity<Object> addProductVariation(String email,
                                                      ProductVariationDto productVariationDto) {

        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }
        Optional<Product> optionalProduct = productRepository.findById(
                productVariationDto.getProductId());
        if (!optionalProduct.isPresent()) {
            throw new ProductException("Product with id " + productVariationDto.getProductId()
                    + " not found.");
        }

        if(!productService.checkOwner(seller,productVariationDto.getProductId()))
        {
            throw new ProductException("You are not owner of the product," +
                    " so you cannot add a product variation ");
        }

        Product product = optionalProduct.get();

        if(!productService.checkProductStatus(product))
        {
            throw new ProductException("Product with id:" + product.getId()
                    + "is either inactive or deleted");
        }

        ProductVariation productVariation = new ProductVariation();
        productVariation.setQuantityAvailable(productVariationDto.getQuantityAvailable());
        productVariation.setPrice(productVariationDto.getPrice());
        productVariation.setProduct(product);
        product.getProductVariationList().add(productVariation);

        Map<String, String> metadataMap = productVariationDto.getMetadataMap();

        if (metadataMap == null || metadataMap.isEmpty()) {
            throw new ProductException("Empty Map");
        }

        boolean isMapValid = productService.checkMapValidity(metadataMap, product);

        if(!isMapValid) {
            throw new ProductException("Metadata map not valid, enter valid fields and values");
        }

        productVariation.setMetadataMap(metadataMap);
        productVariation.setMetadataString(metadataMap.toString());
        productVariationRepository.save(productVariation);
        productRepository.save(product);

        return new ResponseEntity<>("Product Variation added successfully!",
                HttpStatus.OK);
    }

    public ProductVariationViewDto getProductVariation(int productVariationId, String email) {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }
        Optional<ProductVariation> optionalProductVariation = productVariationRepository
                .findById(productVariationId);
        if (!optionalProductVariation.isPresent())
            throw new ProductException("Product Variation does not exist");

        ProductVariation productVariation = optionalProductVariation.get();
        Product product = productVariation.getProduct();

        if(!productService.checkOwner(seller,product.getId()))
        {
            throw new ProductException("You are not owner of the product," +
                    " so you cannot view the product variation. ");
        }

        if(!productService.checkProductStatus(product))
        {
            throw new ProductException("Product with id:" + product.getId() +
                    "is either inactive or deleted");
        }


        return new ProductVariationViewDto(productVariation);
    }

    public List<ProductVariationViewDto> getProductVariationList(String email) {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }
        List<Product> productList = seller.getProductList();
        List<ProductVariation> productVariationList = new ArrayList<>();

        for (Product product : productList) {

            if(productService.checkProductStatus(product)) {
                for (ProductVariation productVariation : product.getProductVariationList()) {
                    productVariationList.add(productVariation);
                }
            }
            else
            {
                throw new ProductException("Product with id:" + product.getId() +
                        "is either inactive or deleted");
            }

        }

        List<ProductVariationViewDto> productVariationViewDtoList = new ArrayList<>();

        for (ProductVariation productVariation : productVariationList) {
            productVariationViewDtoList.add(new ProductVariationViewDto(productVariation));
        }

        return productVariationViewDtoList;
    }


    public ResponseEntity<Object> updateProductVariation(String email,
                                                         ProductVariationUpdateDto productVariationUpdateDto) {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller==null)
        {
            throw new UserNotFoundException("Seller with email: " + email + " not found");
        }
        Optional<ProductVariation> optionalProductVariation = productVariationRepository
                .findById(productVariationUpdateDto.getId());

        if(!optionalProductVariation.isPresent())
        {
            throw new ProductException("Product with id :" + productVariationUpdateDto.getId()
                    + " does not exist");
        }

        ProductVariation productVariation = optionalProductVariation.get();

        if(productVariationUpdateDto.getQuantityAvailable() != 0 )
        {
            productVariation.setQuantityAvailable(productVariationUpdateDto.getQuantityAvailable());
        }

        if(productVariationUpdateDto.getPrice() != 0 )
        {
            productVariation.setPrice(productVariationUpdateDto.getPrice());
        }

        if(productVariationUpdateDto.getMetadataMap()!=null)
        {
            Map<String, String> metadataMapDto = productVariationUpdateDto.getMetadataMap();

            boolean isMapValid = productService.checkMapValidity
                    (metadataMapDto, productVariationRepository.findById(
                                    productVariationUpdateDto.getProductId())
                            .get().getProduct()
                    );

            if(!isMapValid) {
                throw new ProductException("Metadata map not valid, enter valid fields and values");
            }

            productVariation.setMetadataMap(metadataMapDto);
            productVariation.setMetadataString(metadataMapDto.toString());
        }

        productVariationRepository.save(productVariation);

        return new ResponseEntity<Object>("Your product has been updated",HttpStatus.OK);
    }

    public Page<OrderProductViewDto> getOrderList(String email, int pageSize) {
        Seller seller = sellerRepository.findByEmail(email);
        List<Product> productList = seller.getProductList();
        List<ProductVariation> productVariationList = new ArrayList<>();

        for(Product product : productList)
        {
            if(productService.checkProductStatus(product)) {
                List<ProductVariation> productVariationList1 = product.getProductVariationList();

                for (ProductVariation productVariation : productVariationList1) {

                    if(productVariation.isActive())
                        productVariationList.add(productVariation);
                }
            }
        }

        Set<OrderProduct> orderProductSet = new HashSet<>();

        for(ProductVariation productVariation : productVariationList)
        {
            List<OrderProduct> orderProductList1 = orderProductRepository
                    .findByProductVariation(productVariation);

            for(OrderProduct orderProduct : orderProductList1 )
            {
                orderProductSet.add(orderProduct);
            }
        }

        List<OrderProductViewDto> orderProductViewDtoList = new ArrayList<>();
        for (OrderProduct orderProduct : orderProductSet)
        {
            orderProductViewDtoList.add(new OrderProductViewDto(orderProduct,orderProduct.getOrderStatus()));
        }

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id"));
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), orderProductViewDtoList.size());
        final Page<OrderProductViewDto> orderProductViewDtoPage = new PageImpl<OrderProductViewDto>(
                orderProductViewDtoList.subList(start,end), pageable, orderProductViewDtoList.size());
        return orderProductViewDtoPage;

    }

    public ResponseEntity changeOrderState(String email, OrderStateChangeDto orderStateChangeDto) {

        Seller seller = sellerRepository.findByEmail(email);

        Optional<OrderProduct> optionalOrderProduct = orderProductRepository
                .findById(orderStateChangeDto.getOrderProductId());

        OrderProduct orderProduct = optionalOrderProduct.get();

        ProductVariation productVariation = orderProduct.getProductVariation();

        if(!productService.checkOwner(seller,productVariation.getProduct().getId()))
        {
            throw new ProductException("You are not owner of the product," +
                    " so you cannot change order product status ");
        }

        String fromStatus = orderStateChangeDto.getFromStatus().toUpperCase();
        String toStatus = orderStateChangeDto.getToStatus().toUpperCase();

        if(fromStatus.equals(toStatus))
        {
            throw new RuntimeException("Both Status states cannot be same ");
        }
        logger.info("FromStatus : " + fromStatus);
        logger.info("ToStatus : " + toStatus);


        if(FromStatusStates.valueOf(fromStatus)==null)
            throw new RuntimeException("Entered incorrect FROM_STATUS");

        if(ToStatusStates.valueOf(toStatus)==null)
            throw new RuntimeException("Entered incorrect TO_STATUS");

        orderProduct.getOrderStatus().setFromStatus(fromStatus);
        orderProduct.getOrderStatus().setToStatus(toStatus);
        orderProductRepository.save(orderProduct);

        return ResponseEntity.ok("Ordered status is successfully updated");

    }
}