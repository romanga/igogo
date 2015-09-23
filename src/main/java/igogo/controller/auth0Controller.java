package igogo.controller;

import igogo.exception.UserNotFoundException;
import igogo.model.Account;
import igogo.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by rgavrilov on 9/23/15.
 */
@RestController
@RequestMapping("/")
public class auth0Controller {

    private static final Logger log = LoggerFactory.getLogger(auth0Controller.class);

    private final AccountRepository accountRepository;

    @Autowired
    public auth0Controller(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequestMapping(value="/callback", method= RequestMethod.GET)
    Account callBack(@RequestParam(value = "code") String code, @RequestParam(value = "state") String state) {
        log.info("callback called with "+ code + " and state "+state);
        return this.accountRepository.findByEmail("test@test.com")
                .orElseThrow(() -> new UserNotFoundException("test@test.com"));

    }

}
