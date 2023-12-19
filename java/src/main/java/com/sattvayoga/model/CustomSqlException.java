package com.sattvayoga.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CustomSqlException extends RuntimeException {

    public CustomSqlException(String message) {
        super(message);
    }

    public CustomSqlException(String message, Throwable cause) {
        super(message, cause);
    }

}
