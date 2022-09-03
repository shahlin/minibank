package com.example.minibank.account;

import com.example.minibank.customer.Customer;
import com.example.minibank.exceptions.AccountExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public Account openNewAccountForCustomer(Customer customer) {
        Optional<Account> accountOptional = accountRepository.findAccountByCustomerId(customer.getId());

        if (accountOptional.isPresent()) {
            throw new AccountExistsException();
        }

        Account account = new Account();
        account.setCode(generateAccountCode());
        account.setCustomer(customer);
        account.setBalance(0);

        return accountRepository.save(account);
    }

    private String generateAccountCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
