package com.edureka.customer.repository;

import com.edureka.customer.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    List<Customer> findByEmail(String email);
}
