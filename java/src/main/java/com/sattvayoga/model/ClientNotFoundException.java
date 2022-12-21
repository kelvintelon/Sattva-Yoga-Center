package com.sattvayoga.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( value = HttpStatus.NOT_FOUND, reason = "Client Not Found.")
public class ClientNotFoundException extends RuntimeException {

}
