package com.nagarro.supermarket.controller;

import com.nagarro.supermarket.dto.AuthDto;
import com.nagarro.supermarket.dto.LoginDTO;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.AuthenticationService;
import com.nagarro.supermarket.service.OrderService;
import com.nagarro.supermarket.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author rishabhgusain
 *
 */
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        // Mock 
        AuthDto authDto = new AuthDto();
        User user = new User();

        Mockito.when(authenticationService.registerUser(authDto.getUsername(), authDto.getPassword())).thenReturn(user);

        
        User result = authenticationController.registerUser(authDto);

        
        Assertions.assertEquals(user, result);
    }

    @Test
    public void testLoginUser() {
        // Mock input
        AuthDto authDto = new AuthDto();
        
        String expectedJwt = "testjwt";
        LoginDTO expectedLoginDto = new LoginDTO(expectedJwt);
        Mockito.when(authenticationService.loginUser(authDto.getUsername(), authDto.getPassword()))
                .thenReturn(expectedLoginDto);

        LoginDTO result = authenticationController.loginUser(authDto);

        Mockito.verify(authenticationService).loginUser(authDto.getUsername(), authDto.getPassword());
        
        Assertions.assertEquals(expectedLoginDto, result);
    }


    @Test
    public void testInactivateCustomer() {
        // Mock input
        int userId = 1;

        ResponseEntity<String> response = authenticationController.inactivateCustomer(userId);

        Mockito.verify(userService).inactivateUser(userId);

        Mockito.verify(orderService).cancelOrdersByInactiveCustomer(userId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("User inactivated successfully", response.getBody());
    }

    @Test
    public void testActivateCustomer() {
        // Mock
        int userId = 1;
        
        ResponseEntity<String> response = authenticationController.activateCustomer(userId);

        Mockito.verify(userService).activateUser(userId);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("User activated successfully", response.getBody());
    }

    @Test
    public void testGetAllUsers() {
        // Mock input
        List<User> users = new ArrayList<>();

        // Mock dependencies
        Mockito.when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<Object> response = authenticationController.getAllUsers();

        Mockito.verify(userService).getAllUsers();

        // Perform assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(users, ((Map<String, Object>) response.getBody()).get("data"));
    }

}
