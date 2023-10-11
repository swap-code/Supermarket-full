package com.nagarro.supermarket.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author Aakanksha 
 * Class name: IllegalArgumentException 
 * Description: It contains message for IllegalArgument Exception.
 *
 **/

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalArgumentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalArgumentException(String message) {
		super(message);
	}

	public IllegalArgumentException(String message, Throwable cause) {
		super(message, cause);
	}
}
