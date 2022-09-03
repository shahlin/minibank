package com.example.minibank.service;

import com.example.minibank.exception.AccountExistsException;
import com.example.minibank.exception.AccountNotFoundException;
import com.example.minibank.model.Account;
import com.example.minibank.model.Customer;
import com.example.minibank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository);
    }

    @Test
    void canGetAllAccounts() {
        accountService.getAllAccounts();

        verify(accountRepository).findAll();
    }

    @Test
    void canGetAccount() {
        // Given
        String code = UUID.randomUUID().toString();

        Account account = new Account();
        account.setCode(code);
        account.setBalance(0);

        // When
        when(accountRepository.findAccountByCode(code)).thenReturn(Optional.of(account));
        accountService.getAccount(code);

        // Then
        verify(accountRepository).findAccountByCode(code);
    }

    @Test
    void canOpenNewAccountForCustomerWithExistingAccount() {
        String randomCode = UUID.randomUUID().toString();
        Integer id = 1;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setCode(randomCode);
        customer.setEmail("alex@gmail.com");
        customer.setName("Alex");
        customer.setDateOfBirth(LocalDate.of(2000, 1, 1));

        when(accountRepository.findAccountByCustomerId(id)).thenReturn(Optional.empty());

        accountService.openNewAccountForCustomer(customer);

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepository).save(accountArgumentCaptor.capture());
    }

    @Test
    void willThrowAccountNotFoundWhenAccountDoesNotExist() {
        String code = UUID.randomUUID().toString();

        Account account = new Account();
        account.setCode(code);

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(code));
    }

    @Test
    void willThrowAccountExistsIfCustomerAlreadyHasAnAccount() {
        String randomCode = UUID.randomUUID().toString();
        Integer id = 1;

        Customer customer = new Customer();
        customer.setId(id);
        customer.setCode(randomCode);
        customer.setEmail("alex@gmail.com");
        customer.setName("Alex");
        customer.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Account account = new Account();
        account.setCode(randomCode);
        account.setCustomer(customer);

        when(accountRepository.findAccountByCustomerId(id)).thenReturn(Optional.of(account));

        assertThrows(AccountExistsException.class, () -> accountService.openNewAccountForCustomer(customer));
    }
}