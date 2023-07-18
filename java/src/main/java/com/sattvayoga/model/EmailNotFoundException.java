package com.sattvayoga.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( value = HttpStatus.NOT_FOUND, reason = "Email Not Found. Contact The Business Owner")
public class EmailNotFoundException extends RuntimeException{
}
