package com.edureka.customer.controller;

import com.edureka.customer.model.Customer;
import com.edureka.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    public static final Logger _logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository repository;

    /**
     * Get all customers.
     * Returns 200 OK with list of all customers (empty list if none).
     */
    @GetMapping(value = {"", "/", "/all"})
    public ResponseEntity<?> getAllCustomers() {
        _logger.info("Getting all customers");
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     * Create a new customer.
     * Returns 201 Created with the created customer, or 400 for validation errors.
     */
    @PostMapping(value = {"", "/"})
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        if (customer == null) {
            _logger.warn("Customer is null");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Customer body is required"));
        }
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            _logger.warn("Customer email is null or blank");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Customer email is required"));
        }

        customer.setId(UUID.randomUUID().toString());
        Customer saved = repository.save(customer);
        _logger.info("New customer added successfully: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Get customer by email. Used by Order service for inter-service validation.
     * Returns 200 with customer or 404 Not Found.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getCustomerByEmail(@PathVariable String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Customer email is required"));
        }
        _logger.info("Getting customer with email: {}", email);
        return repository.findByEmail(email).stream()
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found for email: " + email)));
    }

    /**
     * Get customer by ID (path variable). Returns 200 with customer or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Customer id is required"));
        }
        _logger.info("Getting customer with id: {}", id);
        return repository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found for id: " + id)));
    }
}
