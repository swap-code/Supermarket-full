package com.nagarro.supermarket.service.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import com.nagarro.supermarket.dao.CartDao;
import com.nagarro.supermarket.dao.CartItemDao;
import com.nagarro.supermarket.dao.ProductDao;
import com.nagarro.supermarket.dao.UserDao;
import com.nagarro.supermarket.dto.CartItemDto;
import com.nagarro.supermarket.dto.ProductDto;
import com.nagarro.supermarket.exceptions.ProductOutOfStockException;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.Cart;
import com.nagarro.supermarket.model.CartItem;
import com.nagarro.supermarket.model.Product;
import com.nagarro.supermarket.model.User;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceImplTest {

	@Mock
	private CartDao cartDao;

	@Mock
	private UserDao userDao;

	@Mock
	private CartItemDao cartItemDao;

	@Mock
	private ProductDao productDao;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private CartServiceImpl cartService;

	private User user;
	private Product product;
	private Cart cart;

	@Before
	public void setup() {
		user = new User();
		user.setUsername("testUser");

		product = new Product();
		product.setProductId(1);
		product.setQuantity(5);
		product.setInStock(true);
		product.setPrice(BigDecimal.valueOf(10.0));

		cart = new Cart();
		cart.setUser(user);
		cart.setItems(new ArrayList<>());
	}

	@Test
	public void testAddProductToCart() {
		when(productDao.findById(1)).thenReturn(Optional.of(product));
		when(modelMapper.map(product, ProductDto.class)).thenReturn(new ProductDto());
		when(cartDao.save(any(Cart.class))).thenReturn(cart);

		cartService.addProductToCart(1, "testUser");

		assertEquals(1, cart.getItems().size());
		assertEquals(4, product.getQuantity());
	}

	@Test(expected = ProductOutOfStockException.class)
	public void testAddProductToCart_OutOfStock() {
		product.setInStock(false);
		when(productDao.findById(1)).thenReturn(Optional.of(product));

		cartService.addProductToCart(1, "testUser");
	}

	@Test
	public void testListCartItems() {
		CartItem cartItem = new CartItem();
		cartItem.setCartItemId(1);
		cartItem.setQuantity(2);
		cartItem.setTotalPrice(BigDecimal.valueOf(20.0));
		cartItem.setProduct(product);

		List<CartItem> cartItems = new ArrayList<>();
		cartItems.add(cartItem);
		cart.setItems(cartItems);

		when(cartDao.findByUser(user)).thenReturn(Optional.of(cart));
		when(modelMapper.map(product, ProductDto.class)).thenReturn(new ProductDto());

		List<CartItemDto> cartItemDtos = cartService.listCartItems("testUser");

		assertEquals(1, cartItemDtos.size());
		assertEquals(1, cartItemDtos.get(0).getCartItemId());
		assertEquals(2, cartItemDtos.get(0).getQuantity());
		assertEquals(BigDecimal.valueOf(20.0), cartItemDtos.get(0).getTotalPrice());
	}

	@Test
	public void testDeleteCartItem() {
		CartItem cartItem = new CartItem();
		cartItem.setCartItemId(1);
		cartItem.setQuantity(2);
		cartItem.setTotalPrice(BigDecimal.valueOf(20.0));
		cartItem.setProduct(product);

		List<CartItem> cartItems = new ArrayList<>();
		cartItems.add(cartItem);
		cart.setItems(cartItems);

		when(cartDao.findByUser(user)).thenReturn(Optional.of(cart));

		cartService.deleteCartItem(1, "testUser");

		assertEquals(0, cart.getItems().size());
		assertEquals(7, product.getQuantity());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteCartItem_NotFound() {
		when(cartDao.findByUser(user)).thenReturn(Optional.of(cart));

		cartService.deleteCartItem(1, "testUser");
	}

	@Test
	public void testIncrementQuantity() {
		CartItem cartItem = new CartItem();
		cartItem.setCartItemId(1);
		cartItem.setQuantity(2);
		cartItem.setTotalPrice(BigDecimal.valueOf(20.0));
		cartItem.setProduct(product);

		List<CartItem> cartItems = new ArrayList<>();
		cartItems.add(cartItem);
		cart.setItems(cartItems);

		when(cartDao.findByUser(user)).thenReturn(Optional.of(cart));
		when(cartItemDao.save(cartItem)).thenReturn(cartItem);

		cartService.incrementQuantity(1, "testUser");

		assertEquals(3, cartItem.getQuantity());
		assertEquals(BigDecimal.valueOf(30.0), cartItem.getTotalPrice());
		assertEquals(4, product.getQuantity());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testIncrementQuantity_NotFound() {
		when(cartDao.findByUser(user)).thenReturn(Optional.of(cart));

		cartService.incrementQuantity(1, "testUser");
	}

	@Test
	public void testDecrementQuantity() {
		CartItem cartItem = new CartItem();
		cartItem.setCartItemId(1);
		cartItem.setQuantity(2);
		cartItem.setTotalPrice(BigDecimal.valueOf(20.0));
		cartItem.setProduct(product);

		List<CartItem> cartItems = new ArrayList<>();
		cartItems.add(cartItem);
		cart.setItems(cartItems);

		when(cartDao.findByUser(user)).thenReturn(Optional.of(cart));
		when(cartItemDao.save(cartItem)).thenReturn(cartItem);

		cartService.decrementQuantity(1, "testUser");

		assertEquals(1, cartItem.getQuantity());
		assertEquals(BigDecimal.valueOf(10.0), cartItem.getTotalPrice());
		assertEquals(6, product.getQuantity());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testDecrementQuantity_NotFound() {
		when(cartDao.findByUser(user)).thenReturn(Optional.of(cart));

		cartService.decrementQuantity(1, "testUser");
	}
}
