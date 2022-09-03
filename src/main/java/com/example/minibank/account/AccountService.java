package com.example.minibank.account;

import com.example.minibank.customer.Customer;
import com.example.minibank.exceptions.AccountExistsException;
import com.example.minibank.exceptions.AccountNotFoundException;
import com.example.minibank.exceptions.CustomerNotFoundException;
import com.example.minibank.transfer.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public Account getAccount(String code) {
        return accountRepository.findAccountByCode(code)
                .orElseThrow(AccountNotFoundException::new);
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

    public Map<String, List<Transfer>> getAllTransfers(String code) {
        Optional<Account> account = accountRepository.findAccountByCode(code);

        if (account.isEmpty()) {
            throw new AccountNotFoundException();
        }

        Map<String, List<Transfer>> transfers = new HashMap<>();
        transfers.put("sent", account.get().getSentTransfers());
        transfers.put("received", account.get().getReceivedTransfers());

        return transfers;
    }

    private String generateAccountCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
