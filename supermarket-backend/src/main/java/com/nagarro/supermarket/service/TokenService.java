package com.nagarro.supermarket.service;

import org.springframework.security.core.Authentication;

/**
 * 
 * @author rishabhgusain
 *
 */

public interface TokenService {

	String generateJwt(Authentication auth);
}
