package com.nagarro.supermarket.service;

import java.util.List;

import com.nagarro.supermarket.model.User;

/**
 * 
 * @author rishabhgusain
 * @author prernakumari
 *
 */

public interface UserService {

	void activateUser(int userId);

	void inactivateUser(int userId);

	List<User> getAllUsers();

	User getUserById(int userId);

	User findUserByUsername(String username);
}
