package com.nagarro.supermarket.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.supermarket.dao.CartDao;
import com.nagarro.supermarket.dao.EmiDao;
import com.nagarro.supermarket.dao.OrderDao;
import com.nagarro.supermarket.dao.ProductDao;
import com.nagarro.supermarket.dao.UserDao;
import com.nagarro.supermarket.dto.EmiDto;
import com.nagarro.supermarket.dto.OrderDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.Cart;
import com.nagarro.supermarket.model.CartItem;
import com.nagarro.supermarket.model.Emi;
import com.nagarro.supermarket.model.Order;
import com.nagarro.supermarket.model.OrderItem;
import com.nagarro.supermarket.model.Product;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.OrderService;
import com.nagarro.supermarket.utils.OrderStatus;

/**
 * 
 * @author rishabhsinghla
 * 
 *         Order service for all order related operations and logic
 *
 */

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private EmiDao emiRepository;

	@Autowired
	private CartDao cartDao;

	@Autowired
	private OrderDao orderRepository;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public void createOrder(User user, EmiDto emiDto) {
		Cart cart = null;
		Optional<Cart> cartOptional = cartDao.findByUser(user);

		if (cartOptional.isPresent()) {
			cart = cartOptional.get();
		}

		if (cart == null || cart.getItems().isEmpty()) {
			throw new ResourceNotFoundException("Cart", "User Id", user.getUserId());
		}

		Order order = new Order();
		if (emiDto != null) {
			Emi newEmi = new Emi();
			newEmi.setMonthlyInstallment(emiDto.getMonthlyInstallment());
			newEmi.setRoi(emiDto.getRoi());
			newEmi.setTenure(emiDto.getTenure());
			order.setUser(user);
			order.setEmi(newEmi);
			emiRepository.save(newEmi);
		} else {
			order.setUser(user);
			order.setEmi(null);
		}

		order.setTotalOrderAmount(cart.getTotalCartAmount());
		order.setStatus(OrderStatus.PROCESSING);

		for (CartItem cartItem : cart.getItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			order.addOrderItem(orderItem);
		}
		orderRepository.save(order);
	}

	@Override
	public List<OrderDto> getAllOrders() {
		List<Order> orders = orderRepository.findAll();
		List<OrderDto> orderDtos = new ArrayList<>();
		for (Order order : orders) {
			List<OrderItem> orderItems = new ArrayList<>(order.getOrderItems());
			orderItems.forEach(orderItem -> orderItem.setOrder(null));
			orderDtos.add(modelMapper.map(order, OrderDto.class));
		}
		return orderDtos;
	}

	@Override
	public OrderDto getOrderById(int orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", "Order ID", orderId));
		return modelMapper.map(order, OrderDto.class);
	}

	@Override
	public List<OrderDto> getOrdersByCustomer(int customerId) {
		List<Order> orders = orderRepository.findByUserUserId(customerId);
		List<OrderDto> orderDtos = new ArrayList<>();
		for (Order order : orders) {
			orderDtos.add(modelMapper.map(order, OrderDto.class));
		}
		return orderDtos;
	}

	@Override
	public void cancelOrdersByInactiveCustomer(int customerId) {
		User user = userDao.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User ID", customerId));
		if (user != null && !user.isActive()) {
			List<Order> orders = orderRepository.findByUser(user);
			for (Order order : orders) {
				if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.COMPLETED) {
					order.setStatus(OrderStatus.CANCELLED);
					List<OrderItem> orderItems = order.getOrderItems();
					for (OrderItem orderItem : orderItems) {
						Product product = orderItem.getProduct();
						int quantityToAdd = orderItem.getQuantity();
						product.setQuantity(product.getQuantity() + quantityToAdd);
						productDao.save(product);
					}
					orderRepository.save(order);
				}
			}
		}
	}

	@Override
	public void updateOrderStatus(int orderId, OrderStatus newStatus) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", "Order ID", orderId));

		if (newStatus == OrderStatus.CANCELLED) {
			if (order.getStatus() == OrderStatus.COMPLETED) {
				throw new IllegalArgumentException("Cannot cancel an already completed order.");
			} else {
				cancelOrder(orderId);
			}
		} else if (newStatus == OrderStatus.COMPLETED) {
			if (order.getStatus() == OrderStatus.CANCELLED) {
				throw new IllegalArgumentException("Cannot mark an already cancelled order as completed.");
			} else {
				order.setStatus(newStatus);
				orderRepository.save(order);
			}
		} else if (newStatus == OrderStatus.PROCESSING) {
			if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
				throw new IllegalArgumentException("Cannot process an already completed or cancelled order.");
			} else {
				order.setStatus(newStatus);
				orderRepository.save(order);
			}
		} else {
			throw new IllegalArgumentException("Invalid order status provided: " + newStatus);
		}
	}

	@Override
	public void cancelOrder(int orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", "Order ID", orderId));
		if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.COMPLETED) {
			order.setStatus(OrderStatus.CANCELLED);
			List<OrderItem> orderItems = order.getOrderItems();
			for (OrderItem orderItem : orderItems) {
				Product product = orderItem.getProduct();
				int quantityToAdd = orderItem.getQuantity();
				product.setQuantity(product.getQuantity() + quantityToAdd);
				productDao.save(product);
			}
			orderRepository.save(order);
		} else {
			throw new IllegalArgumentException("Order is already completed or cancelled");
		}
	}
}
