package com.nagarro.supermarket.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.supermarket.dao.ProductDao;
import com.nagarro.supermarket.dto.ProductDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.Product;
import com.nagarro.supermarket.service.ProductService;

/**
 * @author Rinkaj Solanki Class name: ProductServiceImpl Description: It
 *         contains all the Product Service Implementation methods.
 */

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public void addProduct(ProductDto productDto) {
		Product product = this.modelMapper.map(productDto, Product.class);
		if (productDto.getImageFileName() == null || productDto.getImageFileName().isEmpty()) {
			product.setImageFileName("default_image.png");
		}
		this.productDao.save(product);
	}

	@Override
	public void addProductImage(int productId, String fileName) {
		Product product = productDao.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "Product ID", productId));
		product.setImageFileName(fileName);
		productDao.save(product);
	}

	@Override
	public List<ProductDto> getActiveProducts() {
		List<Product> products = this.productDao.findAll();
		List<ProductDto> productDtos = products.stream().filter(Product::isActive)
				.map((product) -> this.modelMapper.map(product, ProductDto.class)).collect(Collectors.toList());
		return productDtos;
	}

	@Override
	public ProductDto getProductById(int productId) {
		Product product = this.productDao.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));
		return this.modelMapper.map(product, ProductDto.class);
	}

	@Override
	public List<ProductDto> searchProducts(String searchBy, String value) {
		List<Product> products = new ArrayList<>();
		switch (searchBy) {
		case "name":
			products = productDao.findByNameContainingIgnoreCaseOrderByName(value);
			break;
		case "description":
			products = productDao.findByDescriptionContainingIgnoreCaseOrderByDescription(value);
			break;
		case "category":
			products = productDao.findByCategoryContainingIgnoreCaseOrderByCategory(value);
			break;
		default:
			throw new IllegalArgumentException("Invalid searching criteria: " + searchBy);
		}
		if (products.isEmpty()) {
			throw new ResourceNotFoundException();
		}
		return products.stream().map(product -> modelMapper.map(product, ProductDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public void updateProduct(ProductDto productDto) {
		Product product = this.modelMapper.map(productDto, Product.class);
		if (productDto.getImageFileName() == null || productDto.getImageFileName().isEmpty()) {
			product.setImageFileName("default_image.png");
		}
		this.productDao.save(product);
	}

	@Override
	public void updateProductStatus(int productId, boolean newStatus) {
		Product product = productDao.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "Product ID", productId));
		product.setActive(newStatus);
		productDao.save(product);
	}

	@Override
	public void updateProductImage(int productId, String fileName) {
		Product product = productDao.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "Product ID", productId));

		String resourcesFolderPath = "src/main/resources/images/";
		String previousFileName = product.getImageFileName();
		if (previousFileName != null && !previousFileName.equals("default_image.png")) {
			Path previousFilePath = Paths.get(resourcesFolderPath + previousFileName);
			try {
				Files.delete(previousFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		product.setImageFileName(fileName);
		productDao.save(product);
	}

	@Override
	public void deleteProduct(int productId) {
		productDao.deleteById(productId);
	}

	@Override
	public List<Product> getInActiveProduct() {
		List<Product> allProducts = productDao.findAll();
		List<Product> inactiveProducts = new ArrayList<>();
		for (Product product : allProducts) {
			if (!product.isActive()) {
				inactiveProducts.add(product);
			}
		}
		return inactiveProducts;
	}
}