package com.nagarro.supermarket.exceptions;

/**
 * 
 * @author Aakanksha 
 * Class name: ProductOutOfStockException 
 * Description: It contains all ProductOutOfStockException message
 *
 */

public class ProductOutOfStockException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProductOutOfStockException(String message) {
		super(message);
	}
}
