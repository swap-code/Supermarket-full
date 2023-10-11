package com.nagarro.supermarket.service;

import java.util.List;

import com.nagarro.supermarket.dto.ProductDto;
import com.nagarro.supermarket.model.Product;

/**
 * @author Rinkaj Solanki Class name: ProductService Description: It lists all
 *         functions for Product Service Implementation class.
 */

public interface ProductService {

	void addProduct(ProductDto productDto);

	void addProductImage(int productId, String fileName);

	List<ProductDto> getActiveProducts();

	ProductDto getProductById(int prodcutId);

	List<ProductDto> searchProducts(String searchBy, String value);

	void updateProduct(ProductDto productDto);

	void updateProductStatus(int productId, boolean newStatus);

	void deleteProduct(int productId);

	void updateProductImage(int productId, String fileName);

	List<Product> getInActiveProduct();
}
