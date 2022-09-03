package com.example.minibank.customer;

import com.example.minibank.account.Account;
import com.example.minibank.account.AccountService;
import com.example.minibank.exceptions.CustomerEmailTakenException;
import com.example.minibank.exceptions.CustomerIneligibleException;
import com.example.minibank.exceptions.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;

    private static final int CUSTOMER_MIN_AGE_REQUIRED = 18;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, AccountService accountService) {
        this.customerRepository = customerRepository;
        this.accountService = accountService;
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
        validateCustomerExistsWithEmail(customer, "");
        validateAge(customer);

        customer.setCode(generateCustomerCode());

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(String code, Customer customer) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByCode(code);

        if (customerOptional.isEmpty()) {
            throw new CustomerNotFoundException();
        }

        Customer existingCustomer = customerOptional.get();

        validateCustomerExistsWithEmail(customer, existingCustomer.getEmail());
        validateAge(customer);

        existingCustomer.setName(customer.getName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setUpdatedAt(LocalDateTime.now());

        return existingCustomer;
    }

    public Account openNewAccount(String code) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByCode(code);

        if (customerOptional.isEmpty()) {
            throw new CustomerNotFoundException();
        }

        return accountService.openNewAccountForCustomer(customerOptional.get());
    }

    private String generateCustomerCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private void validateCustomerExistsWithEmail(Customer customer, String excludeEmail) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByEmailWithExcludeList(
                customer.getEmail(),
                List.of(excludeEmail)
        );

        if (customerOptional.isPresent()) {
            throw new CustomerEmailTakenException();
        }
    }

    private void validateAge(Customer customer) {
        if (customer.getAge() < CUSTOMER_MIN_AGE_REQUIRED) {
            throw new CustomerIneligibleException("Customer age must be above " + CUSTOMER_MIN_AGE_REQUIRED);
        }
    }
}
