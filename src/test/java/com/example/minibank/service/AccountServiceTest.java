package com.example.minibank.service;

import com.example.minibank.controller.request.DepositRequest;
import com.example.minibank.exception.AccountExistsException;
import com.example.minibank.exception.AccountNotFoundException;
import com.example.minibank.exception.AccountTransactionException;
import com.example.minibank.model.Account;
import com.example.minibank.model.Customer;
import com.example.minibank.model.Transfer;
import com.example.minibank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    void canGetAllTransfersForNewAccount() {
        String code = anyString();

        Account account = new Account();
        account.setId(1);
        account.setCode(code);
        account.setBalance(0);

        when(accountRepository.findAccountByCode(code)).thenReturn(Optional.of(account));

        Map<String, List<Transfer>> expectedTransfers = new HashMap<>();
        expectedTransfers.put("sent", Collections.emptyList());
        expectedTransfers.put("received", Collections.emptyList());

        assertThat(accountService.getAllTransfers(code)).isEqualTo(expectedTransfers);
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
    void canDepositValidAmountIntoAccount() {
        double amountToDeposit = 1000;

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(amountToDeposit);

        String code = anyString();
        Account account = new Account();
        account.setId(1);
        account.setCode(code);
        account.setBalance(0);

        when(accountRepository.findAccountByCode(code)).thenReturn(Optional.of(account));

        accountService.deposit(code, depositRequest);

        assertThat(account.getBalance()).isEqualTo(amountToDeposit);
    }

    @Test
    void willThrowWhenAccountDoesNotExistOnGetSingleAccount() {
        when(accountRepository.findAccountByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(anyString()));
    }

    @Test
    void willThrowWhenAccountDoesNotExistOnGetAllTransfers() {
        when(accountRepository.findAccountByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAllTransfers(anyString()));
    }

    @Test
    void willThrowWhenCustomerAlreadyHasAnAccount() {
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

    @Test
    void willThrowWhenAccountDoesNotExistOnDeposit() {
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(1000);

        when(accountRepository.findAccountByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.deposit(anyString(), depositRequest));
    }

    @Test
    void willThrowWhenDepositAmountIsMoreThanAllowed() {
        double amountToDeposit = 100_000_000;

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(amountToDeposit);

        String code = anyString();
        Account account = new Account();
        account.setId(1);
        account.setCode(code);
        account.setBalance(0);

        when(accountRepository.findAccountByCode(code)).thenReturn(Optional.of(account));

        assertThrows(AccountTransactionException.class, () -> accountService.deposit(code, depositRequest));
    }

    @Test
    void willThrowWhenDepositAmountIsLessThanAllowed() {
        double amountToDeposit = -10;

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(amountToDeposit);

        String code = anyString();
        Account account = new Account();
        account.setId(1);
        account.setCode(code);
        account.setBalance(0);

        when(accountRepository.findAccountByCode(code)).thenReturn(Optional.of(account));

        assertThrows(AccountTransactionException.class, () -> accountService.deposit(code, depositRequest));
    }
}