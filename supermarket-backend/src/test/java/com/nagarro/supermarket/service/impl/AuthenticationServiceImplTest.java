package com.nagarro.supermarket.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nagarro.supermarket.dao.RoleDao;
import com.nagarro.supermarket.dao.UserDao;
import com.nagarro.supermarket.dto.LoginDTO;
import com.nagarro.supermarket.model.Role;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.TokenService;

/**
 * @author rishabhgusain
 * Test cases for authentication service impl
 */
public class AuthenticationServiceImplTest {

	@Mock
	private UserDao userDao;

	@Mock
	private RoleDao roleDao;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private TokenService tokenService;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthenticationServiceImpl authenticationService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testRegisterUser_Success() {
		// Mock input
		String username = "testuser";
		String password = "password";

		String encodedPassword = "encodedPassword";

		// Mock role
		Role userRole = new Role();
		userRole.setAuthority("USER");

		Set<Role> authorities = new HashSet<>();
		authorities.add(userRole);

		// Mock saved user
		User savedUser = new User();
		savedUser.setUserId(1);
		savedUser.setUsername(username);
		savedUser.setPassword(encodedPassword);
		savedUser.setAuthorities(authorities);
		savedUser.setActive(true);

		// Mock DAO operations
		when(roleDao.findByAuthority("USER")).thenReturn(Optional.of(userRole));
		when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
		when(userDao.save(any(User.class))).thenReturn(savedUser);

		// Call the method to test
		User result = authenticationService.registerUser(username, password);

		Assertions.assertEquals(savedUser, result);

		verify(roleDao).findByAuthority("USER");
		verify(passwordEncoder).encode(password);
		verify(userDao).save(any(User.class));
	}

	@Test
	public void testLoginUser_Success() {
		// Mock input
		String username = "testuser";
		String password = "password";

		// Mock authentication token
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

		Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
		when(authenticationManager.authenticate(authToken)).thenReturn(auth);

		String token = "generatedToken";
		when(tokenService.generateJwt(auth)).thenReturn(token);

		LoginDTO loginDto = authenticationService.loginUser(username, password);

		Assertions.assertEquals(token, loginDto.getJwt());

		// Verify interactions
		verify(authenticationManager).authenticate(authToken);
		verify(tokenService).generateJwt(auth);
	}

	@Test
	public void testLoginUser_AuthenticationFailed() {
		// Mock input
		String username = "testuser";
		String password = "password";

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

		when(authenticationManager.authenticate(authToken)).thenThrow(new RuntimeException("User Authentication Failed"));

		Assertions.assertThrows(RuntimeException.class, () -> authenticationService.loginUser(username, password));

		// Verify interactions
		verify(authenticationManager).authenticate(authToken);
	}
}
