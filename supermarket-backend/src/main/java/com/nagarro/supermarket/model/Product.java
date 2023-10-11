package com.nagarro.supermarket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Rinkaj Solanki Class name: Product Description: It contains all the
 *         Product Entity fields.
 */

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int productId;
	private String name;
	private String description;
	private String category;
	private BigDecimal price;
	private int quantity;
	private boolean inStock;
	private String imageFileName;
	private boolean active;
	private LocalDateTime timeStamp = LocalDateTime.now();

	public void setQuantity(int quantity) {
		this.quantity = quantity;
		this.inStock = quantity > 0;
	}
}
