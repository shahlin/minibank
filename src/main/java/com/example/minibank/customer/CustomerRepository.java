package com.example.minibank.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findCustomerByCode(String code);
    Optional<Customer> findCustomerByEmail(String email);
}
