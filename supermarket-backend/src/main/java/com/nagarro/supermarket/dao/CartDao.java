package com.nagarro.supermarket.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.supermarket.model.Cart;
import com.nagarro.supermarket.model.CartItem;
import com.nagarro.supermarket.model.User;

/**
 * 
 * @author Aakanksha Class name: CartDao Description: It contains Cart
 *         Repository.
 *
 **/

@Repository
public interface CartDao extends JpaRepository<Cart, Integer> {

	@Query("SELECT ci FROM CartItem ci WHERE ci.cartItemId = :cartItemId")
	Optional<CartItem> findCartItemById(int cartItemId);

	@Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
	Optional<Cart> findByUserId(int userId);

	Optional<Cart> findByUser(User user);
}
