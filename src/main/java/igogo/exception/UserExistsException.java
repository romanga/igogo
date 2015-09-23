package igogo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rgavrilov on 9/21/15.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserExistsException extends  RuntimeException {

    public UserExistsException(String email) {
        super("user already exists '" + email + "'.");
    }
}
