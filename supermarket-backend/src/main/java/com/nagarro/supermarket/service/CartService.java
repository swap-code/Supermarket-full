package com.nagarro.supermarket.service;

import java.math.BigDecimal;
import java.util.List;

import com.nagarro.supermarket.dto.CartItemDto;
import com.nagarro.supermarket.model.User;

/**
 * 
 * @author Aakanksha Class name: CartService Description: It contains all the
 *         all functions for cart Service Implementation class.
 *
 **/

public interface CartService {

	void addProductToCart(int productId, String username);

	void deleteCartItem(int cartItemId, String username);

	List<CartItemDto> listCartItems(String username);

	void incrementQuantity(int cartItemId, String username);

	void decrementQuantity(int cartItemId, String username);

	BigDecimal calculateTotalCartAmount(String username);

	void clearCartForUser(User user);
}
