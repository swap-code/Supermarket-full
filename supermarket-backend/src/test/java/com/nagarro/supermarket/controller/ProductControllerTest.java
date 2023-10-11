package com.nagarro.supermarket.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nagarro.supermarket.dto.ProductDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.service.ProductService;

/**
 * @author Rinkaj Solanki 
 * Class name: ProductControllerTest  
 * Description: It contains all the ProductController testing methods.
 */


public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddProduct_Success() {
        ProductDto productDto = new ProductDto();

        ResponseEntity<Object> response = productController.addProduct(productDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(productService).addProduct(productDto);
    }


    @Test
    public void testGetActiveProducts_Success() {
        List<ProductDto> products = new ArrayList<>();
        products.add(new ProductDto());
        products.add(new ProductDto());

        when(productService.getActiveProducts()).thenReturn(products);
        ResponseEntity<Object> response = productController.getActiveProducts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).getActiveProducts();
    }

    @Test
    public void testGetProductById_Success() {
        int productId = 1;

        ProductDto product = new ProductDto();
        when(productService.getProductById(productId)).thenReturn(product);
        ResponseEntity<Object> response = productController.getProductById(productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).getProductById(productId);
    }

    @Test
    public void testGetProductById_NotFound() {
        int productId = 1;

        when(productService.getProductById(productId)).thenThrow(ResourceNotFoundException.class);
        ResponseEntity<Object> response = productController.getProductById(productId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).getProductById(productId);
    }


 
    @Test
    public void testGetProductsBySearch_Success() {
        String searchBy = "name";
        String value = "test";

        List<ProductDto> products = new ArrayList<>();
        products.add(new ProductDto());
        products.add(new ProductDto());
        when(productService.searchProducts(searchBy, value)).thenReturn(products);
        ResponseEntity<Object> response = productController.getProductsBySearch(searchBy, value);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).searchProducts(searchBy, value);
    }

    @Test
    public void testGetProductsBySearch_InvalidCriteria() {
        String searchBy = "invalid";
        String value = "test";

        when(productService.searchProducts(searchBy, value)).thenThrow(IllegalArgumentException.class);
        ResponseEntity<Object> response = productController.getProductsBySearch(searchBy, value);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productService).searchProducts(searchBy, value);
    }

    @Test
    public void testGetProductsBySearch_NotFound() {
        String searchBy = "name";
        String value = "test";

        when(productService.searchProducts(searchBy, value)).thenThrow(ResourceNotFoundException.class);
        ResponseEntity<Object> response = productController.getProductsBySearch(searchBy, value);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).searchProducts(searchBy, value);
    }

    @Test
    public void testUpdateProduct_Success() {
        int productId = 1;
        ProductDto productDto = new ProductDto();
        ResponseEntity<Object> response = productController.updateProduct(productId, productDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).updateProduct(productDto);
    }

 
}
