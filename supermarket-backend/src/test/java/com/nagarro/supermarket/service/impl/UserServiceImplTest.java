package com.nagarro.supermarket.service.impl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.nagarro.supermarket.dao.UserDao;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.UserService;



/**
 * @author rishabhgusain
 *
 */
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        when(userDao.findUserByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(username);

        Assertions.assertEquals(username, userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
    
        String username = "testuser";
        when(userDao.findUserByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });
    }

    @Test
    public void testActivateUser_UserExists_ActivatesUser() {

        int userId = 1;
        User user = new User();
        user.setActive(false);
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        userService.activateUser(userId);

        Assertions.assertTrue(user.isActive());
        verify(userDao).save(user);
    }

    @Test
    public void testActivateUser_UserNotFound_ThrowsRuntimeException() {
        // Arrange
        int userId = 1;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.activateUser(userId);
        });
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    public void testInactivateUser_UserExists_InactivatesUser() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setActive(true);
        when(userDao.findById(userId)).thenReturn(Optional.of(user));


        userService.inactivateUser(userId);

        Assertions.assertFalse(user.isActive());
        verify(userDao).save(user);
    }

    @Test
    public void testInactivateUser_UserNotFound_ThrowsRuntimeException() {

        int userId = 1;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.inactivateUser(userId);
        });
        verify(userDao, never()).save(any(User.class));
    }


    @Test
    public void testGetUserById_UserExists_ReturnsUser() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setUserId(userId);
        when(userDao.findByUserId(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        Assertions.assertEquals(userId, result.getUserId());
    }

    @Test
    public void testGetUserById_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        int userId = 1;
        when(userDao.findByUserId(userId)).thenReturn(Optional.empty());

        // Act and Assert
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    public void testFindUserByUsername_UserExists_ReturnsUser() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        when(userDao.findUserByUsername(username)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserByUsername(username);

        // Assert
        Assertions.assertEquals(username, result.getUsername());
    }

    @Test
    public void testFindUserByUsername_UserNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        String username = "testuser";
        when(userDao.findUserByUsername(username)).thenReturn(Optional.empty());

        // Act and Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.findUserByUsername(username);
        });
    }
}
