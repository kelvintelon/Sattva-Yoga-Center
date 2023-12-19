package com.sattvayoga.model;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Database failed to run statement")
public class CustomDataAccessException extends DataAccessException {

    public CustomDataAccessException(String message) {
        super(message);
    }

    public CustomDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
