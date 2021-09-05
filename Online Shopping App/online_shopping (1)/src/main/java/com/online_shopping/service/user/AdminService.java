package com.online_shopping.service.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.online_shopping.dto.*;
import com.online_shopping.entity.order.statusStates.FromStatusStates;
import com.online_shopping.entity.order.Order;
import com.online_shopping.entity.order.OrderProduct;
import com.online_shopping.entity.order.statusStates.ToStatusStates;
import com.online_shopping.entity.product.*;
import com.online_shopping.entity.user.Customer;
import com.online_shopping.entity.user.Seller;
import com.online_shopping.exception.CategoryException;
import com.online_shopping.exception.ProductException;
import com.online_shopping.exception.UserNotFoundException;
import com.online_shopping.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private CustomerRepository customerRepository;
    private SellerRepository sellerRepository;
    private SellerService sellerService;
    private CustomerService customerService;
    private UserService userService;
    private CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    private CategoryRepository categoryRepository;
    private CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;
    private ProductRepository productRepository;
    OrderRepository orderRepository;
    OrderProductRepository orderProductRepository;
    OrderStatusRepository orderStatusRepository;

    @Autowired
    public AdminService(CustomerRepository customerRepository,
                        SellerRepository sellerRepository, SellerService sellerService,
                        CustomerService customerService, UserService userService,
                        CategoryMetadataFieldRepository categoryMetadataFieldRepository,
                        CategoryRepository categoryRepository,
                        CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository,
                        OrderProductRepository orderProductRepository,
                        OrderStatusRepository orderStatusRepository) {

        this.customerRepository = customerRepository;
        this.sellerRepository = sellerRepository;
        this.sellerService = sellerService;
        this.customerService = customerService;
        this.userService = userService;
        this.categoryMetadataFieldRepository = categoryMetadataFieldRepository;
        this.categoryRepository=categoryRepository;
        this.categoryMetadataFieldValuesRepository=categoryMetadataFieldValuesRepository;
        this.productRepository=productRepository;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderStatusRepository = orderStatusRepository;
    }


    public Iterable<CustomerViewDto> getCustomers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Customer> allCustomers = customerRepository.findAll(pageable);

        List<CustomerViewDto> customerViewDtoList = new ArrayList<>();

        for (Customer customer : allCustomers)
        {
            customerViewDtoList.add(new CustomerViewDto(customer));
        }

        return customerViewDtoList;
    }

    public Iterable<SellerViewDto> getSellers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Seller> allSellers = sellerRepository.findAll(pageable);

        List<SellerViewDto> sellerViewDtoList = new ArrayList<>();

        for (Seller seller : allSellers)
        {
            sellerViewDtoList.add(new SellerViewDto(seller));
        }

        return sellerViewDtoList;
    }

    public ResponseEntity<String> activateCustomer(int id) {
        Optional<Customer> customer= customerRepository.findById(id);
        if(customer.isPresent()) {
            Customer customer1 = customer.get();
            if(!customer1.isActive()){
                customer1.setActive(true);
                customerRepository.save(customer1);
                userService.sendActivationMessage(customer1);
                return ResponseEntity.ok("Account is activated");
            }
            else {
                return ResponseEntity.ok("Acount is already active");
            }
        } else {
            throw  new UserNotFoundException("Account not found");
        }
    }

    public ResponseEntity<String> deActivateCustomer(int id) {
        Optional<Customer> customer= customerRepository.findById(id);
        if(customer.isPresent()) {
            Customer customer1 = customer.get();
            if(customer1.isActive()){
                customer1.setActive(false);
                customerRepository.save(customer1);
                userService.sendDeactivationMessage(customer1);
                return ResponseEntity.ok("Account is DeActivated");
            }
            else {
                return ResponseEntity.ok("Acount is already Deactive");
            }
        } else {
            throw  new UserNotFoundException("Account not found");
        }
    }

    public ResponseEntity<String> unlockCustomer(int id) {
        Optional<Customer> customer= customerRepository.findById(id);
        if(customer.isPresent()) {
            Customer customer1 = customer.get();
            userService.resetFailedAttempts(customer1.getEmail());
        }
        else {
            throw  new UserNotFoundException("Account not found");
        }

        return new ResponseEntity("Customer Unlocked",HttpStatus.OK);
    }

    public ResponseEntity<String> activateSeller(int id) {
        Optional<Seller> seller= sellerRepository.findById(id);
        if(seller.isPresent()) {
            Seller seller1 = seller.get();
            if(!seller1.isActive()){
                seller1.setActive(true);
                sellerRepository.save(seller1);
                userService.sendActivationMessage(seller1);
                return ResponseEntity.ok("Account is activated");
            }
            else {
                return ResponseEntity.ok("Account is already active");
            }
        } else {
            throw  new UserNotFoundException("Account not found");
        }
    }

    public ResponseEntity<String> deActivateSeller(int id) {
        Optional<Seller> seller= sellerRepository.findById(id);
        if(seller.isPresent()) {
            Seller seller1 = seller.get();
            if(seller1.isActive()){
                seller1.setActive(false);
                userService.sendDeactivationMessage(seller1);
                return ResponseEntity.ok("Account is Deactivated");
            }
            else {
                return ResponseEntity.ok("Account is already Deactivated");
            }
        } else {
            throw  new UserNotFoundException("Account not found");
        }

    }

    public ResponseEntity<String> unlockSeller(int id) {
        Optional<Seller> seller= sellerRepository.findById(id);
        if(seller.isPresent()) {
            Seller seller1 = seller.get();
            userService.resetFailedAttempts(seller1.getEmail());
        }
        else {
            throw  new UserNotFoundException("Account not found");
        }

        return new ResponseEntity("Seller Unlocked",HttpStatus.OK);
    }

    //category apis

    public String addCategoryMetadataField(CategoryMetadataFieldDto categoryMetadataFieldDto) {

        CategoryMetadataField categoryMetadataField =
                new CategoryMetadataField(categoryMetadataFieldDto.getName());

        categoryMetadataFieldRepository.save(categoryMetadataField);

        return categoryMetadataFieldDto.getName();
    }

    //applied dynamic filtering
    public MappingJacksonValue getCategoryMetadataFiledList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<CategoryMetadataField> categoryMetadataFieldList =
                categoryMetadataFieldRepository.findAll(pageable);

        SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("name");
        FilterProvider filters=new SimpleFilterProvider()
                .addFilter("CategoryMetadataFieldFilter",filter);

        MappingJacksonValue mapping =
                new MappingJacksonValue(categoryMetadataFieldList.getContent());

        mapping.setFilters(filters);

        return mapping;

    }

    public ResponseEntity<String> addCategory(CategoryDto categoryDto) {

        if(categoryDto.getParentId()==null)
        {
            Category category = new Category();
            category.setName(categoryDto.getName());
            categoryRepository.save(category);
            List<Category> categoryList = categoryRepository.findByName(categoryDto.getName());

            for(Category tempCategory : categoryList)
            {
                if(tempCategory.getParentCategory()==null)
                {
                    category=tempCategory;
                    break;
                }
            }

            return new ResponseEntity<>("New " + category.getName() + " category created with id "
                    + category.getId(),
                     HttpStatus.OK);
        }
        else {

            Optional<Category> optionalParentCategory = categoryRepository
                    .findById(categoryDto.getParentId());

            Category parentCategory = optionalParentCategory.get();
            if(parentCategory == null) {
                throw new CategoryException("Parent category not found");
            }

            if(parentCategory.getName().equals(categoryDto.getName()))
            {
                throw new CategoryException("Parent and child category cannot have the same name");
            }

            List<Category> childCategoryList = parentCategory.getChildCategoryList();

            for(Category tempCategory : childCategoryList)
            {
                if(tempCategory.getName().equals(categoryDto.getName()))
                    throw new CategoryException("Category with name '" +
                            categoryDto.getName() + "' " +
                            "already exists in entered parent category," +
                            " enter some other name for the category"  );
            }

            Category category = new Category();
            category.setName(categoryDto.getName());
            category.setParentCategory(parentCategory);

            parentCategory.getChildCategoryList().add(category);

            categoryRepository.save(category);
            categoryRepository.save(parentCategory);

            List<Category> categoryList = categoryRepository.findByName(categoryDto.getName());

            for(Category tempCategory : categoryList)
            {
                //check this
                if(tempCategory.getParentCategory().equals(parentCategory.getName()))
                {
                    category=tempCategory;
                    break;
                }
            }

            return new ResponseEntity<>("New " + category.getName() + " category created with id "
                    + category.getId(),
                    HttpStatus.OK);
        }
    }

    public CategoryViewDto getCategory(int categoryId) {

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        Category category = optionalCategory.get();
        if(category==null)
        {
            throw new CategoryException("Category with id " + categoryId +" does not exist");
        }

        return new CategoryViewDto(category);


    }

    public List<CategoryViewDto> getCategoryList() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Category> categoryList =
                categoryRepository.findAll(pageable);

      List<CategoryViewDto> categoryViewDtoList = new ArrayList<>();

      for(Category category : categoryList)
      {
          categoryViewDtoList.add(new CategoryViewDto(category));
      }

      return categoryViewDtoList;

    }

    public ResponseEntity<Object> updateCategory( int categoryId, CategoryDto categoryDto) {


        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        Category category = optionalCategory.get();

        if(category==null)
        {
            throw new CategoryException("Category with id " + categoryId + " does not exist");
        }

        Category parentCategory = category.getParentCategory();

        if(parentCategory!=null) {

            List<Category> childCategoryList = parentCategory.getChildCategoryList();
            int categoryIndex=-1;
            int indexCounter=0;

            for (Category tempCategory : childCategoryList) {
                if (tempCategory.getName().equalsIgnoreCase(categoryDto.getName())) {
                    throw new CategoryException("Category with name '" +
                            categoryDto.getName() + "' " +
                            "already exists in entered parent category," +
                            " enter some other name for the category");
                }

                if(tempCategory.getName().equals(category.getName())) {
                    categoryIndex = indexCounter;
                }

                indexCounter++;
            }

            category.setName(categoryDto.getName());
            category.setParentCategory(parentCategory);
            parentCategory.getChildCategoryList().remove(categoryIndex);
            parentCategory.getChildCategoryList().add(category);

            categoryRepository.save(category);
            categoryRepository.save(parentCategory);
        }
        else{

            category.setName(categoryDto.getName());
            categoryRepository.save(category);
        }

        return new ResponseEntity<>("Category Updated Successfully",HttpStatus.OK);
    }

    private boolean checkIfCategoryMetadataFieldValuesExists(int categoryMetadataFieldId, int categoryId) {
        return categoryMetadataFieldValuesRepository.findById(categoryMetadataFieldId, categoryId) != null;
    }

    public ResponseEntity<Object> addCategoryMetadataFieldValues(CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto) {
        if (checkIfCategoryMetadataFieldValuesExists(categoryMetadataFieldValuesDto
                .getCategoryMetadataFieldId(), categoryMetadataFieldValuesDto.getCategoryId())) {
            throw new RuntimeException("CategoryMetadataFieldValue With Same Data Already Exists");
        }

        CategoryMetadataFieldValues categoryMetadataFieldValues = new CategoryMetadataFieldValues();
        Category category = categoryRepository.findById(categoryMetadataFieldValuesDto
                .getCategoryId()).get();
        CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository
                .findById(categoryMetadataFieldValuesDto.getCategoryMetadataFieldId()).get();
        categoryMetadataFieldValues.setCategory(category);
        categoryMetadataFieldValues.setCategoryMetadataField(categoryMetadataField);
        categoryMetadataFieldValues.setMetadataValues(categoryMetadataFieldValuesDto.getMetadataValues());
        CategoryMetadataFieldKey categoryMetadataFieldKey = new CategoryMetadataFieldKey(
                categoryMetadataFieldValuesDto.getCategoryMetadataFieldId(),
                categoryMetadataFieldValuesDto.getCategoryId());
        categoryMetadataFieldValues.setCategoryMetadataFieldKey(categoryMetadataFieldKey);
        categoryMetadataFieldValuesRepository.save(categoryMetadataFieldValues);
        return new ResponseEntity<Object>("Metadata value has been added", HttpStatus.OK);
    }

    public ResponseEntity<Object> updateCategoryMetadataValues(
            CategoryMetadataFieldValuesDto categoryMetadataFieldValuesDto) {


        Optional<Category> optionalCategory = categoryRepository.
                findById(categoryMetadataFieldValuesDto.getCategoryId());
        Category category = optionalCategory.get();

        if(category==null) {
            throw new CategoryException("Category does not exist");
        }


        CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository.findById(
                categoryMetadataFieldValuesDto.getCategoryMetadataFieldId()).get();

        if(categoryMetadataField==null)
        {
            throw new CategoryException("Category Metadata Field does not exist");
        }

        CategoryMetadataFieldValues categoryMetadataFieldValues =
                categoryMetadataFieldValuesRepository.findById(categoryMetadataField.getId(),
                        category.getId());

        if(categoryMetadataFieldValues==null)
        {
            throw new CategoryException("Category Metadata Field Value does not exist");
        }

        categoryMetadataFieldValues.setMetadataValues(categoryMetadataFieldValuesDto.getMetadataValues());
        categoryMetadataFieldValuesRepository.save(categoryMetadataFieldValues);

        return new ResponseEntity<Object>("Category Metadata Values Updated Successfully.",
                HttpStatus.OK);

    }

    public ProductViewDto getProduct(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent())
            throw new ProductException("Product Id not found.");
        Product product = optionalProduct.get();
        if (product.isActive() == false)
            throw new RuntimeException("Product has not been activated yet.");

        return new ProductViewDto(product);
    }

    public List<ProductViewDto> getProductList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductViewDto> productViewDtoList = new ArrayList<>();

        for(Product product : productPage)
        {
            productViewDtoList.add(new ProductViewDto(product));

        }

        return productViewDtoList;
    }


    public ResponseEntity<String> activateProduct(int id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product product1 = product.get();
            if (!product1.isActive()) {
                product1.setActive(true);
                productRepository.save(product1);
                return ResponseEntity.ok("Product is activated");
            } else {
                return ResponseEntity.ok("Product is already active");
            }
        } else {
            throw new UserNotFoundException("Product not found");
        }
    }

    public ResponseEntity<String> deactivateProduct(int id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product product1 = product.get();
            if (!product1.isActive()) {
                product1.setActive(false);
                productRepository.save(product1);
                return ResponseEntity.ok("Product is Deactivated");
            } else {
                return ResponseEntity.ok("Product is already Deactivated");
            }
        } else {
            throw new UserNotFoundException("Product not found");
        }
    }

    /* Order Apis */

    public List<OrderViewDto> getOrderList(String name, int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id"));
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderViewDto> orderViewDtoList = new ArrayList<>();
        for(Order order : orderPage)
        {
            orderViewDtoList.add(new OrderViewDto(order));
        }

        return orderViewDtoList;
    }

    public ResponseEntity changeOrderState(String name, OrderStateChangeDto orderStateChangeDto) {

        Optional<OrderProduct> optionalOrderProduct = orderProductRepository
                .findById(orderStateChangeDto.getOrderProductId());

        OrderProduct orderProduct = optionalOrderProduct.get();

        String fromStatus = orderStateChangeDto.getFromStatus().toUpperCase();
        String toStatus = orderStateChangeDto.getToStatus().toLowerCase();

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