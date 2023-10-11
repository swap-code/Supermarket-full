package com.nagarro.supermarket.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.supermarket.model.CartItem;

/**
 * 
 * @author Aakanksha Class name: CartItemDao Description: It contains
 *         CartItemDao Repository.
 *
 **/

@Repository
public interface CartItemDao extends JpaRepository<CartItem, Long> {

}
