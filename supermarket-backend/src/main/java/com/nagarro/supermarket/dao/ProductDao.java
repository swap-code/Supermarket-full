package com.nagarro.supermarket.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.supermarket.model.Product;

/**
 * @author Rinkaj Solanki Class name: ProductDao Description: It contains all
 *         the Product DAO functions.
 */

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {

	List<Product> findByNameContainingIgnoreCaseOrderByName(String name);

	List<Product> findByDescriptionContainingIgnoreCaseOrderByDescription(String description);

	List<Product> findByCategoryContainingIgnoreCaseOrderByCategory(String category);
}