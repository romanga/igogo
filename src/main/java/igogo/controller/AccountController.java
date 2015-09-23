package igogo.controller;

import igogo.exception.UserExistsException;
import igogo.model.Account;
import igogo.exception.UserNotFoundException;
import igogo.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.NoSuchElementException;

/**
 * Created by rgavrilov on 9/21/15.
 */

@RestController
@RequestMapping("/secure/user")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountRepository accountRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequestMapping(value="/{accountId}", method= RequestMethod.GET)
    Account getAccount(@PathVariable Long accountId) {
        this.validateUser(accountId);
        return this.accountRepository.findById(accountId).get();
    }

    @RequestMapping(value="/{accountId}", method = RequestMethod.POST)
    ResponseEntity<?> updateAccount(@PathVariable Long accountId, @RequestBody Account input) {
        accountExists(accountId);
        input.setId(accountId);
        this.accountRepository.save(input);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(input.getId()).toUri()
        );

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addAccount(@RequestBody Account input) {

        this.validateEmail(input.getEmail());

        this.accountRepository.save(input);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(input.getId()).toUri()
        );

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    private void accountExists(Long accountId)
    {
        if (!this.accountRepository.exists(accountId)) {
            throw new UserNotFoundException(accountId);
        }
    }

    private void validateEmail(String email)
    {
        try {
            Account account = this.accountRepository.findByEmail(email).get();
        } catch (NoSuchElementException e) {
            return;
        }

        throw new UserExistsException(email);
    }

    private void validateUser(Long accountId) {
        this.accountRepository.findById(accountId).orElseThrow(
                () -> new UserNotFoundException(accountId));
    }
}
