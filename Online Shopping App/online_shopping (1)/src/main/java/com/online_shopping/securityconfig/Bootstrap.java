package com.online_shopping.securityconfig;


import com.online_shopping.entity.user.*;
import com.online_shopping.repository.CustomerRepository;
import com.online_shopping.repository.RoleRepository;
import com.online_shopping.repository.SellerRepository;
import com.online_shopping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
public class Bootstrap implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    RoleRepository roleRepository;



    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userRepository.count() < 1) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            Role role1 = new Role();
            role1.setAuthority("ROLE_ADMIN");
            role1.setUserList(new ArrayList<>());

            roleRepository.save(role1);

            Role role2 = new Role();
            role2.setAuthority("ROLE_CUSTOMER");
            role2.setUserList(new ArrayList<>());

            roleRepository.save(role2);

            Role role3 = new Role();
            role3.setAuthority("ROLE_SELLER");
            role3.setUserList(new ArrayList<>());

            roleRepository.save(role3);

            //adding admin
            User admin = new User();
            admin.setFirstName("aman");
            admin.setMiddleName("chand");
            admin.setLastName("pareek");
            admin.setEmail("amanpareekap@gmail.com");
            admin.setPassword(passwordEncoder.encode("AmanPareek1!"));
            admin.setActive(true);
            admin.setDeleted(false);
            Role tempRole=roleRepository.findByAuthority("ROLE_ADMIN");

            tempRole.setUserList(Arrays.asList(admin));
            admin.setRoleList(Arrays.asList(tempRole));

            userRepository.save(admin);

/*
            //adding sample customer
            Customer customer=new Customer();
            customer.setFirstName("harshit");
            customer.setMiddleName("chand");
            customer.setLastName("pareek");
            customer.setEmail("harsh.prk6696@gmail.com");
            customer.setPassword(passwordEncoder.encode("AmanPareek1!"));
            customer.setActive(true);
            customer.setDeleted(false);

            tempRole=roleRepository.findByAuthority("ROLE_CUSTOMER");
            customer.setRoleList(Arrays.asList(tempRole));

            customer.setContactList(Arrays.asList(9898989999L,9898989999L));

            Address address=new Address();
            address.setAddressLine("14 A shanti nagar jhotwara");
            address.setCity("jaipur");
            address.setState("raj");
            address.setCountry("india");
            address.setZipCode(302012);
            address.setLabel("home");
            address.setCustomer(customer);
            customer.setAddressList(Arrays.asList(address));

            customerRepository.save(customer);


            //adding sample seller
            Seller seller= new Seller();
            seller.setFirstName("aman");
            seller.setMiddleName("chand");
            seller.setLastName("pareek");
            seller.setEmail("amancrpareek@gmail.com");
            //customer.setPassword(passwordEncoder.encode("AmanPareek1!"));
            seller.setPassword(passwordEncoder.encode("AmanPareek1!"));
            seller.setActive(true);
            seller.setDeleted(false);
            seller.setCompanyName("abc pvt.ltd");
            seller.setGst("77ADBDE2341N3ZQ");

            tempRole=roleRepository.findByAuthority("ROLE_SELLER");
            seller.setRoleList(Arrays.asList(tempRole));

            customer.setContactList(Arrays.asList(9898989999L,9898989999L));

            address=new Address();
            address.setAddressLine("14 A shanti nagar jhotwara");
            address.setCity("jaipur");
            address.setState("raj");
            address.setCountry("india");
            address.setZipCode(302012);
            address.setLabel("home");
            address.setSeller(seller);
            seller.setAddress(address);

            sellerRepository.save(seller);
*/

        }
    }
}
