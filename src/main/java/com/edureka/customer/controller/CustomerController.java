package com.edureka.customer.controller;

import com.edureka.customer.model.Customer;
import com.edureka.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    public static final Logger _logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository repository;

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

        customer.setId(customer.getId() != null && !customer.getId().isBlank()
                ? customer.getId() : UUID.randomUUID().toString());
        Customer saved = repository.save(customer);
        _logger.info("New customer added successfully: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Get all customers. Optional filter by email.
     * Returns 200 OK with list (empty list if none or no match).
     */
    @GetMapping(value = {"", "/", "/all"})
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(required = false) String email) {
        _logger.info("Getting all customers" + (email != null && !email.isBlank() ? " for email: " + email : ""));
        List<Customer> customers = email != null && !email.isBlank()
                ? repository.findByEmail(email)
                : repository.findAll();
        return ResponseEntity.ok(customers);
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
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found for id: " + id)));
    }

    /**
     * Update an existing customer by ID.
     * Returns 200 OK with updated customer, 404 if not found, 400 for validation errors.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Customer id is required"));
        }
        if (customer == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Customer body is required"));
        }
        if (customer.getEmail() != null && customer.getEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Customer email cannot be blank"));
        }

        Optional<Customer> existing = repository.findById(id);
        if (existing.isEmpty()) {
            _logger.warn("Customer not found for update: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Customer not found for id: " + id));
        }

        Customer toUpdate = existing.get();
        if (customer.getFirstName() != null) toUpdate.setFirstName(customer.getFirstName());
        if (customer.getLastName() != null) toUpdate.setLastName(customer.getLastName());
        if (customer.getEmail() != null) toUpdate.setEmail(customer.getEmail());

        Customer updated = repository.save(toUpdate);
        _logger.info("Customer updated successfully: {}", id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a customer by ID.
     * Returns 204 No Content on success, 404 if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Customer id is required"));
        }
        if (!repository.existsById(id)) {
            _logger.warn("Customer not found for delete: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Customer not found for id: " + id));
        }
        repository.deleteById(id);
        _logger.info("Customer deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
