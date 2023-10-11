package com.nagarro.supermarket.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.supermarket.model.Order;
import com.nagarro.supermarket.model.User;

/**
 * 
 * @author rishabhsinghla
 *
 */

@Repository
public interface OrderDao extends JpaRepository<Order, Integer> {

	List<Order> findByUser(User user);

	List<Order> findByUserUserId(int userId);
}
