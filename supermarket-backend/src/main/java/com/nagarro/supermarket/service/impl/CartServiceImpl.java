package com.nagarro.supermarket.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.nagarro.supermarket.service.CartService;

/**
 * 
 * @author Aakanksha Class name: CartServiceImpl Description: It contains all
 *         the methods for cart Service .
 *
 **/

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartDao cartDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CartItemDao cartItemDao;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ProductDao productDao;

	@Override
	public void addProductToCart(int productId, String username) {
		Product product = productDao.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "Product ID", productId));

		ProductDto productDto = modelMapper.map(product, ProductDto.class);

		if (!productDto.isInStock()) {
			throw new ProductOutOfStockException("Product is out of stock.");
		}
		int updatedQuantity = product.getQuantity() - 1;
		product.setQuantity(updatedQuantity);

		productDao.save(product);
		Cart cart = getOrCreateCart(username);
		List<CartItem> cartItems = cart.getItems();

		Optional<CartItem> cartItemOptional = cartItems.stream()
				.filter(cartItem -> cartItem.getProduct().equals(product)).findFirst();

		if (cartItemOptional.isPresent()) {
			CartItem cartItem = cartItemOptional.get();
			int quantity = cartItem.getQuantity() + 1;
			cartItem.setQuantity(quantity);

			BigDecimal totalPrice = productDto.getPrice().multiply(BigDecimal.valueOf(quantity));
			cartItem.setTotalPrice(totalPrice);

			cartItemDao.save(cartItem);
		} else {
			CartItem cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setQuantity(1);

			BigDecimal totalPrice = productDto.getPrice();
			cartItem.setTotalPrice(totalPrice);

			cartItem.setCart(cart);
			cartItemDao.save(cartItem);

			cartItems.add(cartItem);
			cartDao.save(cart);
		}

		BigDecimal totalCartAmount = calculateTotalCartAmount(username);
		cart.setTotalCartAmount(totalCartAmount);
		cartDao.save(cart);
	}

	@Override
	public List<CartItemDto> listCartItems(String username) {
		Cart cart = getOrCreateCart(username);
		List<CartItem> cartItems = cart.getItems();
		Map<Integer, CartItemDto> cartItemMap = new HashMap<>();

		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			ProductDto productDto = modelMapper.map(product, ProductDto.class);
			int productId = product.getProductId();

			if (cartItemMap.containsKey(productId)) {
				CartItemDto existingCartItem = cartItemMap.get(productId);
				int newQuantity = existingCartItem.getQuantity() + cartItem.getQuantity();
				BigDecimal newTotalPrice = existingCartItem.getTotalPrice().add(cartItem.getTotalPrice());
				existingCartItem.setQuantity(newQuantity);
				existingCartItem.setTotalPrice(newTotalPrice);
			} else {
				CartItemDto cartItemDto = new CartItemDto();
				cartItemDto.setCartItemId(cartItem.getCartItemId());
				cartItemDto.setQuantity(cartItem.getQuantity());
				cartItemDto.setTotalPrice(cartItem.getTotalPrice());
				cartItemDto.setProduct(productDto);
				cartItemMap.put(productId, cartItemDto);
			}
		}
		return new ArrayList<>(cartItemMap.values());
	}

	@Override
	public void deleteCartItem(int cartItemId, String username) {
		Cart cart = getOrCreateCart(username);
		Optional<CartItem> cartItemOptional = findCartItemById(cartItemId, cart.getItems());
		if (cartItemOptional.isPresent()) {
			CartItem cartItem = cartItemOptional.get();

			Product product = cartItem.getProduct();
			increaseProductQuantity(product, cartItem.getQuantity());

			cart.getItems().remove(cartItem);
			cartItemDao.delete(cartItem);

			cartDao.save(cart);

			BigDecimal totalCartAmount = calculateTotalCartAmount(username);
			cart.setTotalCartAmount(totalCartAmount);
			cartDao.save(cart);
		} else {
			throw new ResourceNotFoundException("Cart Item", "ID", cartItemId);
		}
	}

	private void increaseProductQuantity(Product product, int quantity) {
		product.setQuantity(product.getQuantity() + quantity);
		productDao.save(product);
	}

	@Override
	public void incrementQuantity(int cartItemId, String username) {
		Cart cart = getOrCreateCart(username);
		Optional<CartItem> cartItemOptional = findCartItemById(cartItemId, cart.getItems());
		if (cartItemOptional.isPresent()) {
			CartItem cartItem = cartItemOptional.get();
			int newQuantity = cartItem.getQuantity() + 1;
			cartItem.setQuantity(newQuantity);

			Product product = cartItem.getProduct();
			reduceProductQuantity(product);

			BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(newQuantity));
			cartItem.setTotalPrice(totalPrice);

			cartItemDao.save(cartItem);
			cartDao.save(cart);

			BigDecimal totalCartAmount = calculateTotalCartAmount(username);
			cart.setTotalCartAmount(totalCartAmount);
			cartDao.save(cart);
		} else {
			throw new ResourceNotFoundException("Cart Item", "ID", cartItemId);
		}
	}

	@Override
	public void decrementQuantity(int cartItemId, String username) {
		Cart cart = getOrCreateCart(username);
		Optional<CartItem> cartItemOptional = findCartItemById(cartItemId, cart.getItems());
		if (cartItemOptional.isPresent()) {
			CartItem cartItem = cartItemOptional.get();
			int newQuantity = cartItem.getQuantity() - 1;
			if (newQuantity <= 0) {
				cart.getItems().remove(cartItem);
				cartItemDao.delete(cartItem);

				Product product = cartItem.getProduct();
				increaseProductQuantity(product);
			} else {
				cartItem.setQuantity(newQuantity);

				Product product = cartItem.getProduct();
				increaseProductQuantity(product);

				BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(newQuantity));
				cartItem.setTotalPrice(totalPrice);

				cartItemDao.save(cartItem);
			}

			cartDao.save(cart);

			BigDecimal totalCartAmount = calculateTotalCartAmount(username);
			cart.setTotalCartAmount(totalCartAmount);
			cartDao.save(cart);
		} else {
			throw new ResourceNotFoundException("Cart Item", "ID", cartItemId);
		}
	}

	private void reduceProductQuantity(Product product) {
		int currentQuantity = product.getQuantity();
		if (currentQuantity > 0) {
			product.setQuantity(currentQuantity - 1);
			productDao.save(product);
		} else {
			throw new ProductOutOfStockException("Product is out of stock.");
		}
	}

	private void increaseProductQuantity(Product product) {
		product.setQuantity(product.getQuantity() + 1);
		productDao.save(product);
	}

	@Override
	public BigDecimal calculateTotalCartAmount(String username) {
		Cart cart = getOrCreateCart(username);
		List<CartItem> cartItems = cart.getItems();
		if (cartItems == null || cartItems.isEmpty()) {
			return BigDecimal.ZERO;
		}

		BigDecimal totalCartAmount = BigDecimal.ZERO;
		for (CartItem cartItem : cartItems) {
			totalCartAmount = totalCartAmount.add(cartItem.getTotalPrice());
		}
		return totalCartAmount;
	}

	private Cart getOrCreateCart(String username) {

		Optional<User> userOptional = userDao.findUserByUsername(username);
		User user = userOptional
				.orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

		Optional<Cart> cartOptional = cartDao.findByUser(user);
		if (cartOptional.isPresent()) {
			return cartOptional.get();
		} else {
			Cart newCart = new Cart();
			newCart.setUser(user);
			return cartDao.save(newCart);
		}
	}

	private Optional<CartItem> findCartItemById(int cartItemId, List<CartItem> cartItems) {
		return cartItems.stream().filter(cartItem -> cartItem.getCartItemId() == cartItemId).findFirst();
	}

	private BigDecimal calculateTotalCartAmount(Cart cart) {
		List<CartItem> cartItems = cart.getItems();
		if (cartItems == null || cartItems.isEmpty()) {
			return BigDecimal.ZERO;
		}

		BigDecimal totalCartAmount = BigDecimal.ZERO;
		for (CartItem cartItem : cartItems) {
			totalCartAmount = totalCartAmount.add(cartItem.getTotalPrice());
		}
		return totalCartAmount;
	}

	@Override
	public void clearCartForUser(User user) {
		Optional<Cart> cartOptional = cartDao.findByUser(user);
		if (cartOptional.isPresent()) {
			Cart cart = cartOptional.get();
			List<CartItem> cartItemsToRemove = new ArrayList<>();

			for (CartItem cartItem : cart.getItems()) {
				cartItemsToRemove.add(cartItem);
			}

			for (CartItem cartItem : cartItemsToRemove) {
				cart.getItems().remove(cartItem);
				cartItemDao.delete(cartItem);
			}

			BigDecimal totalCartAmount = calculateTotalCartAmount(cart);
			cart.setTotalCartAmount(totalCartAmount);
			cartDao.save(cart);
		}
	}
}
