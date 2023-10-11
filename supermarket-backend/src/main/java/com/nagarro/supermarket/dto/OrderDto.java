package com.nagarro.supermarket.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.nagarro.supermarket.model.Emi;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.utils.OrderStatus;

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
public class OrderDto {

	private int orderId;
	private User userId;
	private List<OrderItemDto> orderItems = new ArrayList<>();
	private Emi emiId;
	private OrderStatus status;
	private BigDecimal totalOrderAmount;
}
