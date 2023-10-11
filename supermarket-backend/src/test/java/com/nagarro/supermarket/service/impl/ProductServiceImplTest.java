package com.nagarro.supermarket.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import com.nagarro.supermarket.dao.ProductDao;
import com.nagarro.supermarket.dto.ProductDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.Product;

/**
 * @author Rinkaj Solanki 
 * Class name: ProductServiceImplTest  
 * Description: It contains all the ProductServiceImpl testing methods.
 */

public class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetActiveProducts_Success() {
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setActive(true);
        products.add(product1);
        Product product2 = new Product();
        product2.setActive(true);
        products.add(product2);

        when(productDao.findAll()).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(new ProductDto());

        List<ProductDto> result = productService.getActiveProducts();

        assertEquals(2, result.size());
        verify(productDao).findAll();
        verify(modelMapper, times(2)).map(any(Product.class), eq(ProductDto.class));
    }

    @Test
    public void testGetProductById_Success() {
        Product product = new Product();
        product.setProductId(1);

        Optional<Product> optionalProduct = Optional.of(product);
        when(productDao.findById(1)).thenReturn(optionalProduct);
        ProductDto productDto = new ProductDto();
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1);

        assertEquals(productDto, result);
        verify(productDao).findById(1);
        verify(modelMapper).map(product, ProductDto.class);
    }

    @Test
    public void testGetProductById_ProductNotFound() {
        Optional<Product> optionalProduct = Optional.empty();
        when(productDao.findById(1)).thenReturn(optionalProduct);

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1));
        verify(productDao).findById(1);
        verifyNoMoreInteractions(productDao);
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testSearchProducts_SearchByName_Success() {
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        products.add(new Product());

        when(productDao.findByNameContainingIgnoreCaseOrderByName(anyString())).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(new ProductDto());
        List<ProductDto> result = productService.searchProducts("name", "test");
        assertEquals(2, result.size());
        verify(productDao).findByNameContainingIgnoreCaseOrderByName("test");
        verify(modelMapper, times(2)).map(any(Product.class), eq(ProductDto.class));
    }

    @Test
    public void testSearchProducts_InvalidSearchBy() {
        assertThrows(IllegalArgumentException.class, () -> productService.searchProducts("invalid", "test"));
        verifyNoInteractions(productDao);
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testGetInActiveProduct_Success() {
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setActive(false);
        products.add(product1);
        Product product2 = new Product();
        product2.setActive(false);
        products.add(product2);

        when(productDao.findAll()).thenReturn(products);
        List<Product> result = productService.getInActiveProduct();
        assertEquals(2, result.size());
        verify(productDao).findAll();
    }
}
