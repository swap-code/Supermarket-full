package com.nagarro.supermarket.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.supermarket.dto.EmiDto;
import com.nagarro.supermarket.dto.OrderDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.CartService;
import com.nagarro.supermarket.service.OrderService;
import com.nagarro.supermarket.service.UserService;
import com.nagarro.supermarket.utils.OrderStatus;
import com.nagarro.supermarket.utils.ResponseHandler;

/**
 * 
 * @author rishabhsinghla
 * 
 *         User and Admin Order related operations
 *
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;
	private final UserService userService;
	private final CartService cartService;
	private static final Logger logger = Logger.getLogger(OrderController.class);

	@Autowired
	public OrderController(OrderService orderService, UserService userService, CartService cartService) {
		this.orderService = orderService;
		this.userService = userService;
		this.cartService = cartService;
	}

	@PostMapping("")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> createOrder(@RequestBody(required = false) EmiDto emiDto,
			Authentication authentication) {
		try {
			String username = authentication.getName();
			User user = userService.findUserByUsername(username);
			orderService.createOrder(user, emiDto);
			cartService.clearCartForUser(user);
			logger.info("Order added successfully for user: " + username);
			return ResponseHandler.generateResponse("Order added successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Failed to create order: " + e.getMessage());
			return ResponseHandler.generateResponse("Failed to create order", HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/{orderId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<Object> getOrderById(@PathVariable("orderId") int orderId) {
		OrderDto orderDto = orderService.getOrderById(orderId);
		if (orderDto != null) {
			logger.info("Order fetched successfully.");
			return ResponseHandler.generateResponse("Order fetched successfully", HttpStatus.OK, orderDto);
		} else {
			logger.warn("Order not found with ID: " + orderId);
			return ResponseHandler.generateResponse("Order not found", HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/customer")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<Object> getOrdersByCustomer(Authentication authentication) {
		String username = authentication.getName();
		User user = userService.findUserByUsername(username);
		int customerId = user.getUserId();
		List<OrderDto> orderDtos = orderService.getOrdersByCustomer(customerId);
		if (orderDtos != null) {
			return ResponseHandler.generateResponse("Orders Fetched Successfully", HttpStatus.OK, orderDtos);
		} else {
			return ResponseHandler.generateResponse("Orders not found", HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/customer/cancelorders")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> cancelAllOrdersOfCustomer(Authentication authentication) {
		try {
			String username = authentication.getName();
			User user = userService.findUserByUsername(username);
			int customerId = user.getUserId();
			orderService.cancelOrdersByInactiveCustomer(customerId);
			logger.info("Cancelled all orders for customer: " + username);
			return ResponseHandler.generateResponse("All customers order cancelled", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Failed to cancel orders: " + e.getMessage());
			return ResponseHandler.generateResponse("Orders not found", HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> getAllOrders() {
		List<OrderDto> orderDtos = new ArrayList<>();
		try {
			orderDtos = orderService.getAllOrders();
		} catch (ResourceNotFoundException e) {
			logger.error("Error in getting orders: " + e.getMessage());
			return ResponseHandler.generateResponse("Error in getting orders", HttpStatus.NOT_FOUND);
		}
		return ResponseHandler.generateResponse("Orders Fetched Successfully", HttpStatus.OK, orderDtos);
	}

	@PutMapping("/cancel/{orderId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<Object> cancelOrder(@PathVariable("orderId") int orderId) {
		try {
			orderService.cancelOrder(orderId);
			logger.info("Order cancelled successfully. Order ID: " + orderId);
			return ResponseHandler.generateResponse("Order Cancelled Successfully", HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			logger.warn("Order not found with ID: " + orderId);
			return ResponseHandler.generateResponse("Order not found", HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/status/update/{orderId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> updateOrderStatus(@PathVariable("orderId") int orderId, @RequestBody String status) {
		try {
			OrderStatus orderStatus = null;
			if (status.contains("completed") || status.contains("COMPLETED")) {
				orderStatus = OrderStatus.COMPLETED;
			}
			if (status.contains("cancelled") || status.contains("CANCELLED")) {
				orderStatus = OrderStatus.CANCELLED;
			}
			if (status.contains("processing") || status.contains("PROCESSING")) {
				orderStatus = OrderStatus.PROCESSING;
			}
			if (orderStatus == null) {
				throw new IllegalArgumentException("Invalid order status provided: " + orderStatus);
			}
			orderService.updateOrderStatus(orderId, orderStatus);
			logger.info("Order marked as completed. Order ID: " + orderId);
			return ResponseHandler.generateResponse("Update the order status", HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			logger.warn("Order not found with ID: " + orderId);
			return ResponseHandler.generateResponse("Order not found", HttpStatus.NOT_FOUND);
		}
	}
}
