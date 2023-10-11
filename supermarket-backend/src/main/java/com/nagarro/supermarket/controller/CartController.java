package com.nagarro.supermarket.controller;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.supermarket.dto.CartItemDto;
import com.nagarro.supermarket.service.impl.CartServiceImpl;
import com.nagarro.supermarket.utils.ResponseHandler;

/**
 * 
 * @author Aakanksha 
 * Class name: CartController 
 * Description: It contains all the CART APIs.
 *
 **/

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/cart")
public class CartController {

	private final CartServiceImpl cartService;
	private static final Logger logger = Logger.getLogger(CartController.class);
	

	@Autowired
	public CartController(CartServiceImpl cartService) {
		this.cartService = cartService;
	}

	@PostMapping("/addProduct/{productId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> addProductToCart(@PathVariable("productId") int productId,
			Authentication authentication) {
		try {
			String username = authentication.getName();
			cartService.addProductToCart(productId, username);
			logger.info("Product added to cart successfully.");
			return ResponseHandler.generateResponse("Product added to cart successfully.", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error occurred while adding product to cart: " + e.getMessage());
			return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deleteItem/{cartItemId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> deleteCartItem(@PathVariable("cartItemId") int cartItemId,
			Authentication authentication) {
		try {
			String username = authentication.getName();
			cartService.deleteCartItem(cartItemId, username);
			logger.info("Cart item deleted successfully.");
			return ResponseHandler.generateResponse("Cart item deleted successfully.", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error occurred while deleting cart item: " + e.getMessage());
			return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{cartItemId}/incrementQuantity")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> incrementQuantity(@PathVariable("cartItemId") int cartItemId,
			Authentication authentication) {
		try {
			String username = authentication.getName();
			cartService.incrementQuantity(cartItemId, username);
			logger.info("Quantity incremented successfully.");
			return ResponseHandler.generateResponse("Quantity incremented successfully.", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error occurred while incrementing quantity: " + e.getMessage());
			return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{cartItemId}/decrementQuantity")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> decrementQuantity(@PathVariable("cartItemId") int cartItemId,
			Authentication authentication) {
		try {
			String username = authentication.getName();
			cartService.decrementQuantity(cartItemId, username);
			logger.info("Quantity decremented successfully.");
			return ResponseHandler.generateResponse("Quantity decremented successfully.", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error occurred while decrementing quantity: " + e.getMessage());
			return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/totalAmount")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> getTotalCartAmount(Authentication authentication) {
		try {
			String username = authentication.getName();
			BigDecimal totalAmount = cartService.calculateTotalCartAmount(username);
			logger.info("Total cart amount fetched successfully.");
			return ResponseHandler.generateResponse("Total cart amount fetched successfully.", HttpStatus.OK,
					totalAmount);
		} catch (Exception e) {
			logger.error("Error occurred while fetching total cart amount: " + e.getMessage());
			return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> getAllCartItems(Authentication authentication) {
		try {
			String username = authentication.getName();
			List<CartItemDto> itemsInCart = cartService.listCartItems(username);
			logger.info("Fetched all cart items.");
			return ResponseHandler.generateResponse("All cart items fetched.", HttpStatus.OK,
					itemsInCart);
		} catch (Exception e) {
			logger.error("Error occurred while fetching cart items: " + e.getMessage());
			return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
