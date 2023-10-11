package com.nagarro.supermarket.service;

import java.util.List;

import com.nagarro.supermarket.dto.EmiDto;
import com.nagarro.supermarket.dto.OrderDto;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.utils.OrderStatus;

/**
 * 
 * @author rishabhsinghla
 *
 */

public interface OrderService {

	void createOrder(User user, EmiDto emiDto);

	void cancelOrder(int orderId);

	List<OrderDto> getAllOrders();

	OrderDto getOrderById(int orderId);

	List<OrderDto> getOrdersByCustomer(int customerId);

	void cancelOrdersByInactiveCustomer(int customerId);
	
	void updateOrderStatus(int orderId, OrderStatus newStatus);
}
