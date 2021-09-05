package com.online_shopping.repository;

import com.online_shopping.entity.user.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<Address,Integer> {

    List<Address> findByCustomerId(int customerId);
}
