package com.nagarro.supermarket.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.supermarket.dto.AuthDto;
import com.nagarro.supermarket.dto.LoginDTO;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.AuthenticationService;
import com.nagarro.supermarket.service.OrderService;
import com.nagarro.supermarket.service.UserService;
import com.nagarro.supermarket.utils.ResponseHandler;

/**
 * @author rishabhgusain
 * @author prernakumari
 *
 *         This class is for user login, register and user related operations
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {
	
	private static final Logger logger = Logger.getLogger(AuthenticationController.class);
	

	private final AuthenticationService authenticationService;
	private final UserService userService;
	private final OrderService orderService;
	

	@Autowired
	public AuthenticationController(AuthenticationService authenticationService, UserService userService,
			OrderService orderService) {
		this.authenticationService = authenticationService;
		this.userService = userService;
		this.orderService = orderService;
	}

	@PostMapping("/register")
	public User registerUser(@RequestBody AuthDto body) {
		logger.info("Registering user: " + body.getUsername());
		return authenticationService.registerUser(body.getUsername(), body.getPassword());
	}

	@PostMapping("/login")
	public LoginDTO loginUser(@RequestBody AuthDto body) {
		logger.info("login successful");
		return authenticationService.loginUser(body.getUsername(), body.getPassword());
	}

	@PostMapping("/{userId}/inactivate")
	@PreAuthorize("hasRole('ADMIN')")
	@CrossOrigin("*")
	public ResponseEntity<String> inactivateCustomer(@PathVariable int userId) {
		userService.inactivateUser(userId);
		orderService.cancelOrdersByInactiveCustomer(userId);
		logger.info("User inactivated successfully");
		return ResponseEntity.ok("User inactivated successfully");
	}

	@PostMapping("/{userId}/activate")
	@PreAuthorize("hasRole('ADMIN')")
	@CrossOrigin("*")
	public ResponseEntity<String> activateCustomer(@PathVariable int userId) {
		userService.activateUser(userId);
		logger.info("User activated successfully");
		return ResponseEntity.ok("User activated successfully");
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> getAllUsers() {
		List<User> user = userService.getAllUsers();
		logger.info("Fetched all users");
		return ResponseHandler.generateResponse("Users fetched successfully", HttpStatus.OK, user);
	}
}
