package com.sattvayoga.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( value = HttpStatus.BAD_REQUEST, reason = "Email Already Exists.")
public class EmailAlreadyExistsException extends RuntimeException{

}
