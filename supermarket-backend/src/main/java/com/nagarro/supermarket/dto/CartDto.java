package com.nagarro.supermarket.dto;

import java.util.ArrayList;
import java.util.List;

import com.nagarro.supermarket.model.CartItem;
import com.nagarro.supermarket.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Aakanksha , 
 * Class name: CartDto 
 * Description: It contains all the CartDto attributes
 *
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
	
	private int cartId;

	private User user;

	private List<CartItem> items = new ArrayList<>();
}
