package igogo.controller;

import igogo.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rgavrilov on 9/22/15.
 */

@ControllerAdvice
public aspect AccountControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(AccountControllerAdvice.class);

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors userNotFoundExceptionHandler(Exception ex) {
        log.error("error: "+ex.getMessage());
        return new VndErrors("error", ex.getMessage());

    }


    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors unhandledException(Exception ex) {
        log.error("error: "+ex.getMessage());
        return new VndErrors("error", ex.getMessage());

    }
}
