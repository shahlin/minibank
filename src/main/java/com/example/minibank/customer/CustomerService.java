package com.example.minibank.customer;

import com.example.minibank.exceptions.CustomerEmailTakenException;
import com.example.minibank.exceptions.CustomerIneligibleException;
import com.example.minibank.exceptions.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private static final int CUSTOMER_MIN_AGE_REQUIRED = 18;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomer(String code) {
        return customerRepository.findCustomerByCode(code)
                .orElseThrow(CustomerNotFoundException::new);
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(customer.getEmail());

        if (customerOptional.isPresent()) {
            throw new CustomerEmailTakenException();
        }

        if (customer.getAge() < CUSTOMER_MIN_AGE_REQUIRED) {
            throw new CustomerIneligibleException("Customer age must be above " + CUSTOMER_MIN_AGE_REQUIRED);
        }

        customer.setCode(generateCustomerCode());

        return customerRepository.save(customer);
    }

    private String generateCustomerCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
