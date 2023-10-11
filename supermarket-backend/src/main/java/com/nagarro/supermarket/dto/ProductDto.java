package com.nagarro.supermarket.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Rinkaj Solanki Class name: ProductDto Description: It contains all
 *         the Product DTO fields.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

	private int productId;
	private String name;
	private String description;
	private String category;
	private BigDecimal price;
	private int quantity;
	private boolean inStock = true;
	private String imageFileName;
	private boolean active = true;
}
