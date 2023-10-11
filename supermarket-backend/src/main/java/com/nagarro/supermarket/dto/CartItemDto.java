package com.nagarro.supermarket.dto;

import java.math.BigDecimal;

import com.nagarro.supermarket.model.Cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Aakanksha Class name: CartItemDto Description: It contains all the
 *         CartItemDto attributes
 *
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

	private int cartItemId;
	private Cart cart;
	private ProductDto product;
	private int quantity;
	private BigDecimal totalPrice;
}
