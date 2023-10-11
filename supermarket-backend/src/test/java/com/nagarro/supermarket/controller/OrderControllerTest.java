package com.nagarro.supermarket.controller;
import com.nagarro.supermarket.dto.EmiDto;
import com.nagarro.supermarket.dto.OrderDto;
import com.nagarro.supermarket.dto.OrderItemDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.CartService;
import com.nagarro.supermarket.service.OrderService;
import com.nagarro.supermarket.service.UserService;
import com.nagarro.supermarket.utils.ResponseHandler;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;


/**
 * @author rishabhgusain
 *
 */
public class OrderControllerTest {

    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(orderService, userService, cartService);
    }


    @Test
    public void testCreateOrder_ValidRequest_ReturnCreated() {
        // Mock 
        EmiDto emiDto = new EmiDto();
        
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");

       
        User user = new User();
        Mockito.when(userService.findUserByUsername("username")).thenReturn(user);

      
        Mockito.doNothing().when(orderService).createOrder(user, emiDto);

       
        ResponseEntity<Object> response = orderController.createOrder(emiDto, authentication);

       
        Mockito.verify(orderService).createOrder(user, emiDto);
        Mockito.verify(cartService).clearCartForUser(user);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verify the response body
        Map<String, Object> expectedResponseBody = new HashMap<>();
        expectedResponseBody.put("message", "Order added successfully");
        expectedResponseBody.put("data", null);

        @SuppressWarnings("unchecked")
        Map<String, Object> actualResponseBody = (Map<String, Object>) response.getBody();
        Assertions.assertEquals(expectedResponseBody.get("message"), actualResponseBody.get("message"));
        Assertions.assertEquals(expectedResponseBody.get("data"), actualResponseBody.get("data"));
    }


    @Test
    public void testCreateOrder_ExceptionThrown_ReturnNotFound() throws Exception {
        // Mock 
        EmiDto emiDto = new EmiDto();

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");

        Map<String, User> users = new HashMap<>();
        User user = new User();
        users.put("username", user);
        Mockito.when(userService.findUserByUsername("username")).thenReturn(users.get("username"));

        Mockito.doThrow(new RuntimeException("Failed to create order")).when(orderService).createOrder(users.get("username"), emiDto);

        ResponseEntity<Object> response = orderController.createOrder(emiDto, authentication);


        Mockito.verify(orderService).createOrder(users.get("username"), emiDto);
        Mockito.verify(cartService, Mockito.never()).clearCartForUser(users.get("username"));

        // Perform assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify the response body
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        Assertions.assertEquals("Failed to create order", responseBody.get("message"));
    }


    @Test
    public void testGetOrderById_NonExistingOrderId_ReturnNotFound() throws JsonProcessingException {
        // Mock
        int orderId = 1;

        Mockito.when(orderService.getOrderById(orderId)).thenReturn(null);

        ResponseEntity<Object> response = orderController.getOrderById(orderId);

        Mockito.verify(orderService).getOrderById(orderId);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Order not found");
        expectedResponse.put("status", "NOT_FOUND");

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedResponseJson = objectMapper.writeValueAsString(expectedResponse);
        String actualResponseJson = objectMapper.writeValueAsString(response.getBody());

        Assertions.assertEquals(expectedResponseJson, actualResponseJson);
    }

    
    @Test
    public void testGetOrdersByCustomer_ExistingCustomer_ReturnOkWithOrderDtos() {
        // Mock
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");

        User user = new User();
        user.setUserId(1);
        Mockito.when(userService.findUserByUsername("username")).thenReturn(user);

        List<OrderDto> orderDtos = new ArrayList<>();
        orderDtos.add(new OrderDto());

        Mockito.when(orderService.getOrdersByCustomer(user.getUserId())).thenReturn(orderDtos);


        ResponseEntity<Object> response = orderController.getOrdersByCustomer(authentication);

        Mockito.verify(userService).findUserByUsername("username");
        Mockito.verify(orderService).getOrdersByCustomer(user.getUserId());

        // Perform assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        HashMap<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Orders Fetched Successfully");
        responseBody.put("data", orderDtos);

        HashMap<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Orders Fetched Successfully");
        expectedResponse.put("data", orderDtos);

        Assertions.assertEquals(expectedResponse, responseBody);
    }

    @Test
    public void testGetOrdersByCustomer_NonExistingCustomer_ReturnNotFound() {
        // Mock 
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");

        User user = new User();
        Mockito.when(userService.findUserByUsername("username")).thenReturn(user);

        Mockito.when(orderService.getOrdersByCustomer(user.getUserId())).thenReturn(null);

        // Call the method to test
        ResponseEntity<Object> response = orderController.getOrdersByCustomer(authentication);

        Mockito.verify(userService).findUserByUsername("username");
        Mockito.verify(orderService).getOrdersByCustomer(user.getUserId());

        // Perform assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify the response body
        Map<String, Object> expectedResponseBody = new HashMap<>();
        expectedResponseBody.put("message", "Orders not found");
        expectedResponseBody.put("data", null);

        @SuppressWarnings("unchecked")
        Map<String, Object> actualResponseBody = (Map<String, Object>) response.getBody();
        Assertions.assertEquals(expectedResponseBody.get("message"), actualResponseBody.get("message"));
        Assertions.assertEquals(expectedResponseBody.get("data"), actualResponseBody.get("data"));
    }




    @Test
    public void testCancelAllOrdersOfCustomer_ExistingCustomer_ReturnOk() throws JsonProcessingException {
        // Mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");

        // Mock user
        User user = new User();
        user.setUserId(1);
        Mockito.when(userService.findUserByUsername("username")).thenReturn(user);

        Mockito.doNothing().when(orderService).cancelOrdersByInactiveCustomer(user.getUserId());

        ResponseEntity<Object> response = orderController.cancelAllOrdersOfCustomer(authentication);

        Mockito.verify(userService).findUserByUsername("username");
        Mockito.verify(orderService).cancelOrdersByInactiveCustomer(user.getUserId());

        // Perform assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "All customers order cancelled");
        expectedResponse.put("status", "OK");

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        String actualJson = objectMapper.writeValueAsString(response.getBody());

        Assertions.assertEquals(expectedJson, actualJson);
    }
    
    @Test
    public void testCancelAllOrdersOfCustomer_ExceptionThrown_ReturnNotFound() throws CancellationException {
        // Mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("username");

        // Mock user
        User user = new User();
        Mockito.when(userService.findUserByUsername("username")).thenReturn(user);

        Mockito.doThrow(new CancellationException("Cancellation failed")).when(orderService).cancelOrdersByInactiveCustomer(user.getUserId());

        ResponseEntity<Object> response = orderController.cancelAllOrdersOfCustomer(authentication);

        Mockito.verify(userService).findUserByUsername("username");
        Mockito.verify(orderService).cancelOrdersByInactiveCustomer(user.getUserId());

        // Perform assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals("Orders not found", responseBody.get("message"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseBody.get("status"));
    }



    @Test
    public void testGetAllOrders_ExistingOrders_ReturnOkWithOrderDtos() throws ResourceNotFoundException {
        // Mock order DTOs
        List<OrderDto> orderDtos = new ArrayList<>();
        orderDtos.add(new OrderDto());

        // Mock order retrieval
        Mockito.when(orderService.getAllOrders()).thenReturn(orderDtos);

        ResponseEntity<Object> response = orderController.getAllOrders();

        Mockito.verify(orderService).getAllOrders();

        // Perform assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        
        Assertions.assertEquals("Orders Fetched Successfully", responseBody.get("message"));
        Assertions.assertEquals(orderDtos, responseBody.get("data"));
    }



    @Test
    public void testGetAllOrders_ExceptionThrown_ReturnNotFound() throws ResourceNotFoundException {
        // Mock order retrieval exception
        Mockito.when(orderService.getAllOrders()).thenThrow(ResourceNotFoundException.class);

        // Call the method to test
        ResponseEntity<Object> response = orderController.getAllOrders();

        // Verify the order retrieval
        Mockito.verify(orderService).getAllOrders();

        // Perform assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify the response body
        Map<String, Object> expectedResponseBody = new HashMap<>();
        expectedResponseBody.put("message", "Error in getting orders");
        expectedResponseBody.put("data", null);

        @SuppressWarnings("unchecked")
        Map<String, Object> actualResponseBody = (Map<String, Object>) response.getBody();
        Assertions.assertEquals(expectedResponseBody.get("message"), actualResponseBody.get("message"));
        Assertions.assertEquals(expectedResponseBody.get("data"), actualResponseBody.get("data"));
    }

    @Test
    public void testCancelOrder_ExistingOrderId_ReturnOk() throws ResourceNotFoundException {
        // Mock input
        int orderId = 1;

        Mockito.doNothing().when(orderService).cancelOrder(orderId);

        ResponseEntity<Object> response = orderController.cancelOrder(orderId);

        Mockito.verify(orderService).cancelOrder(orderId);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> expectedResponseBody = new HashMap<>();
        expectedResponseBody.put("message", "Order Cancelled Successfully");
        expectedResponseBody.put("data", null);

        @SuppressWarnings("unchecked")
        Map<String, Object> actualResponseBody = (Map<String, Object>) response.getBody();
        Assertions.assertEquals(expectedResponseBody.get("message"), actualResponseBody.get("message"));
        Assertions.assertEquals(expectedResponseBody.get("data"), actualResponseBody.get("data"));
    }


    @Test
    public void testCancelOrder_NonExistingOrderId_ReturnNotFound() throws Exception {
        // Mock input
        int orderId = 1;

        Mockito.doThrow(ResourceNotFoundException.class).when(orderService).cancelOrder(orderId);

        ResponseEntity<Object> response = orderController.cancelOrder(orderId);

        Mockito.verify(orderService).cancelOrder(orderId);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        String expectedJson = "{\"message\":\"Order not found\",\"status\":\"NOT_FOUND\"}";
        String actualJson = new ObjectMapper().writeValueAsString(response.getBody());

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT);
    }


    @Test
    public void testMarkOrderAsCompleted_ExistingOrderId_ReturnOk() throws ResourceNotFoundException {
        // Mock input
        int orderId = 1;

        Mockito.doNothing().when(orderService).markOrderAsCompleted(orderId);

        ResponseEntity<Object> response = orderController.markOrderAsCompleted(orderId);

        Mockito.verify(orderService).markOrderAsCompleted(orderId);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        Map<String, Object> expectedResponseBody = new HashMap<>();
        expectedResponseBody.put("message", "Order marked as completed");
        expectedResponseBody.put("data", null);

        @SuppressWarnings("unchecked")
        Map<String, Object> actualResponseBody = (Map<String, Object>) response.getBody();
        Assertions.assertEquals(expectedResponseBody.get("message"), actualResponseBody.get("message"));
        Assertions.assertEquals(expectedResponseBody.get("data"), actualResponseBody.get("data"));
    }


    @Test
    public void testMarkOrderAsCompleted_NonExistingOrderId_ReturnNotFound() throws ResourceNotFoundException, JsonProcessingException {
        // Mock input
        int orderId = 1;

        Mockito.doThrow(ResourceNotFoundException.class).when(orderService).markOrderAsCompleted(orderId);

        ResponseEntity<Object> response = orderController.markOrderAsCompleted(orderId);

        Mockito.verify(orderService).markOrderAsCompleted(orderId);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Order not found");
        expectedResponse.put("status", "NOT_FOUND");

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        String actualJson = objectMapper.writeValueAsString(response.getBody());

        Assertions.assertEquals(expectedJson, actualJson);
    }



}

