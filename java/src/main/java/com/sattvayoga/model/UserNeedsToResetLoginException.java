package com.sattvayoga.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( value = HttpStatus.BAD_REQUEST, reason = "User Needs To Reset Login.")
public class UserNeedsToResetLoginException extends RuntimeException {
}
