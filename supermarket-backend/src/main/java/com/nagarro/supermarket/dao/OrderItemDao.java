package com.nagarro.supermarket.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.supermarket.model.OrderItem;

/**
 * 
 * @author rishabhsinghla
 *
 */

@Repository
public interface OrderItemDao extends JpaRepository<OrderItem, Integer> {

}
