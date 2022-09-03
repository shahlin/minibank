package com.example.minibank.controller.api;

import com.example.minibank.service.AccountService;
import com.example.minibank.model.Account;
import com.example.minibank.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accountsList = accountService.getAllAccounts();

        return new ResponseEntity<>(accountsList, HttpStatus.OK);
    }

    @GetMapping(path = "{code}")
    public ResponseEntity<Account> getAccount(@PathVariable("code") String code) {
        Account account = accountService.getAccount(code);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping(path = "{code}/transfers")
    public ResponseEntity<Map<String, List<Transfer>>> getAllTransfers(@PathVariable("code") String code) {
        Map<String, List<Transfer>> transfers = accountService.getAllTransfers(code);

        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

}
