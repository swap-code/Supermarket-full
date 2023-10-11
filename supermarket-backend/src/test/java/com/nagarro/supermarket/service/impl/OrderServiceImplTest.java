package com.nagarro.supermarket.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

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
import com.nagarro.supermarket.model.Product;
import com.nagarro.supermarket.model.User;

/**
 * 
 * @author rishabhsinghla
 *
 */

public class OrderServiceImplTest {

	@Mock
	private EmiDao emiRepository;

	@Mock
	private ProductDao productRepository;

	@Mock
	private CartDao cartDao;

	@Mock
	private OrderDao orderRepository;

	@Mock
	private UserDao userDao;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private OrderServiceImpl orderService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	 @Test
	    public void testCreateOrder_Success() {
	        // Mock user
	        User user = new User();
	        user.setUserId(1);

	        // Mock cart and cart items
	        Cart cart = new Cart();
	        List<CartItem> cartItems = new ArrayList<>();
	        Product product = new Product();
	        product.setQuantity(10);
	        CartItem cartItem = new CartItem();
	        cartItem.setProduct(product);
	        cartItem.setQuantity(5);
	        cartItems.add(cartItem);
	        cart.setItems(cartItems);

	        // Mock EmiDto
	        EmiDto emiDto = new EmiDto();
	        emiDto.setMonthlyInstallment(BigDecimal.valueOf(1000));
	        emiDto.setRoi(BigDecimal.valueOf(10.25));
	        emiDto.setTenure(12);

	        // Mock optional cart
	        Optional<Cart> optionalCart = Optional.of(cart);
	        when(cartDao.findByUser(user)).thenReturn(optionalCart);

	        // Mock product repository
	        when(productRepository.save(any(Product.class))).thenReturn(product);

	        // Mock order repository
	        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

	        // Mock the modelMapper.map() method
	        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto());

	        // Call the method to test
	        Assertions.assertDoesNotThrow(() -> orderService.createOrder(user, emiDto));

	        // Verify interactions
	        verify(cartDao).findByUser(user);
	        verify(productRepository).save(any(Product.class));
	        verify(orderRepository).save(any(Order.class));
	        verify(modelMapper).map(any(Order.class), eq(OrderDto.class));
	    }

	@Test
	public void testCreateOrder_CartNotFound() {
		// Mock user
		User user = new User();
		user.setUserId(1);

		// Mock optional cart as empty
		Optional<Cart> optionalCart = Optional.empty();
		when(cartDao.findByUser(user)).thenReturn(optionalCart);

		// Call the method to test and verify the exception
		Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(user, new EmiDto()));

		// Verify interactions
		verify(cartDao).findByUser(user);
		verifyNoMoreInteractions(productRepository, orderRepository);
	}

	@Test
	public void testGetAllOrders_Success() {
		// Mock order list
		List<Order> orders = new ArrayList<>();
		orders.add(new Order());
		orders.add(new Order());

		// Mock order repository
		when(orderRepository.findAll()).thenReturn(orders);

		// Call the method to test
		List<OrderDto> orderDtoList = orderService.getAllOrders();

		// Verify the result
		Assertions.assertEquals(2, orderDtoList.size());

		// Verify interactions
		verify(orderRepository).findAll();
	}

	@Test
	public void testGetAllOrders_EmptyList() {
		// Mock empty order list
		List<Order> orders = new ArrayList<>();

		// Mock order repository
		when(orderRepository.findAll()).thenReturn(orders);

		// Call the method to test
		List<OrderDto> orderDtoList = orderService.getAllOrders();

		// Verify the result
		Assertions.assertEquals(0, orderDtoList.size());

		// Verify interactions
		verify(orderRepository).findAll();
	}

	@Test
	public void testGetOrderById_Success() {
		// Mock order
		Order order = new Order();
		order.setOrderId(1);

		// Mock optional order
		Optional<Order> optionalOrder = Optional.of(order);
		when(orderRepository.findById(1)).thenReturn(optionalOrder);

		// Mock model mapper
		OrderDto orderDto = new OrderDto();
		when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);

		// Call the method to test
		OrderDto result = orderService.getOrderById(1);

		// Verify the result
		Assertions.assertEquals(orderDto, result);

		// Verify interactions
		verify(orderRepository).findById(1);
		verify(modelMapper).map(order, OrderDto.class);
	}

	@Test
	public void testGetOrderById_OrderNotFound() {
		// Mock optional order as empty
		Optional<Order> optionalOrder = Optional.empty();
		when(orderRepository.findById(1)).thenReturn(optionalOrder);

		// Call the method to test and verify the exception
		Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1));

		// Verify interactions
		verify(orderRepository).findById(1);
		verifyNoMoreInteractions(modelMapper);
	}

	@Test
	public void testGetOrdersByCustomerId_EmptyList() {
		// Mock empty order list
		List<Order> orders = new ArrayList<>();

		// Mock order repository
		when(orderRepository.findByUserUserId(1)).thenReturn(orders);

		// Call the method to test
		List<OrderDto> orderDtoList = orderService.getOrdersByCustomer(1);

		// Verify the result
		Assertions.assertEquals(0, orderDtoList.size());

		// Verify interactions
		verify(orderRepository).findByUserUserId(1);
		verifyNoMoreInteractions(modelMapper);
	}

	@Test
	public void testCancelOrdersByInactiveCustomer_CustomerInactive() {
		// Mock user
		User user = new User();
		user.setUserId(1);
		user.setActive(false);

		// Mock order list
		List<Order> orders = new ArrayList<>();
		Order order1 = new Order();
		order1.setOrderId(1);
		order1.setCompleted(false);
		Order order2 = new Order();
		order2.setOrderId(2);
		order2.setCompleted(true);
		orders.add(order1);
		orders.add(order2);

		// Mock user service
		Optional<User> optionalUser = Optional.of(user);
		when(userDao.findById(1)).thenReturn(optionalUser);

		// Mock order repository
		when(orderRepository.findByUser(user)).thenReturn(orders);

		// Call the method to test
		orderService.cancelOrdersByInactiveCustomer(1);

		// Verify interactions
		verify(userDao).findById(1);
		verify(orderRepository).findByUser(user);
		verify(orderRepository).save(order1);
		verifyNoMoreInteractions(orderRepository);
	}

	@Test
	public void testCancelOrdersByInactiveCustomer_CustomerActive() {
		// Mock user
		User user = new User();
		user.setUserId(1);
		user.setActive(true);

		// Mock user service
		Optional<User> optionalUser = Optional.of(user);
		when(userDao.findById(1)).thenReturn(optionalUser);

		// Call the method to test
		orderService.cancelOrdersByInactiveCustomer(1);

		// Verify interactions
		verify(userDao).findById(1);
		verifyNoInteractions(orderRepository);
	}

	@Test
	public void testMarkOrderAsCompleted_Success() {
		// Mock order
		Order order = new Order();
		order.setOrderId(1);

		// Mock optional order
		Optional<Order> optionalOrder = Optional.of(order);
		when(orderRepository.findById(1)).thenReturn(optionalOrder);

		// Call the method to test
		Assertions.assertDoesNotThrow(() -> orderService.markOrderAsCompleted(1));

		// Verify interactions
		verify(orderRepository).findById(1);
		verify(orderRepository).save(order);
	}

	@Test
	public void testMarkOrderAsCompleted_OrderNotFound() {
		// Mock optional order as empty
		Optional<Order> optionalOrder = Optional.empty();
		when(orderRepository.findById(1)).thenReturn(optionalOrder);

		// Call the method to test and verify the exception
		Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.markOrderAsCompleted(1));

		// Verify interactions
		verify(orderRepository).findById(1);
		verifyNoMoreInteractions(orderRepository);
	}
}
