package com.nagarro.supermarket.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nagarro.supermarket.dto.ProductDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.Product;
import com.nagarro.supermarket.service.ProductService;
import com.nagarro.supermarket.utils.ResponseHandler;

/**
 * @author Rinkaj Solanki Class name: ProductController Description: It contains
 *         all the Product APIs.
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;
	private static final Logger logger = Logger.getLogger(ProductController.class);

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> addProduct(@RequestBody ProductDto productDto) {
		try {
			productService.addProduct(productDto);
			logger.info("Product added successfully");
			return ResponseHandler.generateResponse("Product added successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Failed to add product: " + e.getMessage());
			return ResponseHandler.generateResponse("Failed to add product", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/add-product-image/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> addProductImage(@PathVariable("productId") int productId,
			@RequestPart("imageFile") MultipartFile imageFile) {
		try {
			String fileName = imageFile.getOriginalFilename();

			// Save the image file to the appropriate location
			String resourcesFolderPath = "src/main/resources/images/";
			Path filePath = Paths.get(resourcesFolderPath + fileName);
			Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			productService.addProductImage(productId, fileName);
			logger.info("Product image added successfully");
			return ResponseHandler.generateResponse("Product image added successfully", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Failed to add product image: " + e.getMessage());
			return ResponseHandler.generateResponse("Failed to add product image", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("")
	public ResponseEntity<Object> getActiveProducts() {
		List<ProductDto> products = productService.getActiveProducts();
		return ResponseHandler.generateResponse("Products fetched successfully", HttpStatus.OK, products);
	}

	@GetMapping("{productId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<Object> getProductById(@PathVariable("productId") int productId) {
		try {
			ProductDto product = productService.getProductById(productId);
			logger.info("Product fetched successfully");
			return ResponseHandler.generateResponse("Product fetched successfully", HttpStatus.OK, product);
		} catch (ResourceNotFoundException e) {
			logger.warn("Product not found with ID: " + productId);
			return ResponseHandler.generateResponse("Product not found", HttpStatus.NOT_FOUND, productId);
		}
	}

	@GetMapping("/images/{imageName}")
	public ResponseEntity<Resource> getProductImage(@PathVariable("imageName") String imageName) {
		try {
			// Define the path where the images are stored
			String resourcesFolderPath = "src/main/resources/images/";
			Path imagePath = Paths.get(resourcesFolderPath + imageName);

			// Load the image file
			Resource resource = new UrlResource(imagePath.toUri());

			if (resource.exists()) {
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"")
						.body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("Failed to fetch product image: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/searchBy/{searchBy}/{value}")
	public ResponseEntity<Object> getProductsBySearch(@PathVariable("searchBy") String searchBy,
			@PathVariable("value") String value) {
		try {
			System.out.println("sus");
			List<ProductDto> products = productService.searchProducts(searchBy, value);
			logger.info("Products search by " + searchBy + " fetched successfully");
			return ResponseHandler.generateResponse("Products search by " + searchBy + " fetched successfully",
					HttpStatus.OK, products);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid searching criteria: " + e.getMessage());
			return ResponseHandler.generateResponse("Invalid searching criteria", HttpStatus.BAD_REQUEST,
					Collections.emptyList());
		} catch (ResourceNotFoundException e) {
			logger.warn("Product not found");
			return ResponseHandler.generateResponse("Product not found", HttpStatus.NOT_FOUND);
		}

	}

	@PutMapping("{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> updateProduct(@PathVariable("productId") int productId,
			@RequestBody ProductDto productDto) {
		try {
			productDto.setProductId(productId);
			productService.updateProduct(productDto);
			logger.info("Product updated successfully. Product ID: " + productId);
			return ResponseHandler.generateResponse("Product updated successfully", HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			logger.warn("Product not found with ID: " + productId);
			return ResponseHandler.generateResponse("Product not found", HttpStatus.NOT_FOUND, productId);
		} catch (Exception e) {
			logger.error("Failed to update product: " + e.getMessage());
			return ResponseHandler.generateResponse("Failed to update product", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update-product-image/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> updateProductImage(@PathVariable("productId") int productId,
			@RequestPart("imageFile") MultipartFile imageFile) {
		try {
			String fileName = imageFile.getOriginalFilename();

			// Save the updated image file to the appropriate location
			String resourcesFolderPath = "src/main/resources/images/";
			Path filePath = Paths.get(resourcesFolderPath + fileName);
			Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			productService.updateProductImage(productId, fileName);
			logger.info("Product image updated successfully");
			return ResponseHandler.generateResponse("Product image updated successfully", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Failed to update product image: " + e.getMessage());
			return ResponseHandler.generateResponse("Failed to update product image", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/status/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> changeProductStatus(@PathVariable int productId,
			@RequestBody Map<String, Boolean> requestBody) {
		boolean newStatus = requestBody.get("active");
		productService.updateProductStatus(productId, newStatus);
		logger.info("Product status changed successfully. Product ID: " + productId);
		return ResponseEntity.status(HttpStatus.OK).body("Product status changed successfully.");
	}

	@DeleteMapping("{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Object> deleteProduct(@PathVariable("productId") int productId) {
		try {
			productService.deleteProduct(productId);
			logger.info("Product deleted successfully. Product ID: " + productId);
			return ResponseHandler.generateResponse("Product deleted successfully", HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			logger.warn("Product not found with ID: " + productId);
			return ResponseHandler.generateResponse("Product not found", HttpStatus.NOT_FOUND, productId);
		}
	}

	@GetMapping("/inactive")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Product>> getInactiveProducts() {
		List<Product> products = this.productService.getInActiveProduct();
		return ResponseEntity.ok(products);
	}
}
