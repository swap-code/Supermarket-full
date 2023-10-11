package com.nagarro.supermarket.service;

import com.nagarro.supermarket.dto.LoginDTO;
import com.nagarro.supermarket.model.User;

/**
 * 
 * @author rishabhgusain
 *
 */

public interface AuthenticationService {

	User registerUser(String username, String password);

	LoginDTO loginUser(String username, String password);
}
