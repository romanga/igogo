package igogo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rgavrilov on 9/21/15.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends  RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(UserNotFoundException.class);

    public UserNotFoundException(Long accountId) {
        super("could not find user '" + accountId.toString() + "'.");
        log.error("user not found: "+accountId.toString());
    }

    public UserNotFoundException(String username) {
        super("could not find user '" + username + "'.");
        log.error("user not found: " + username);
    }
}
