package com.nagarro.supermarket.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rishabhsinghla
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

	private int orderItemId;
	private int productId;
	private int quantity;
	private BigDecimal totalPrice;
}
