package com.nagarro.supermarket.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;


/**
 * @author rishabhgusain
 *
 */
public class TokenServiceImplTest {

	@Mock
	private JwtEncoder jwtEncoder;

	@InjectMocks
	private TokenServiceImpl tokenService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testGenerateJwt_Success() {
		// Mock input
		String username = "testuser";
		Collection<? extends GrantedAuthority> authorities = List.of(() -> "ROLE_ADMIN", () -> "ROLE_USER");

		// Mock authentication
		Authentication authentication = new Authentication() {
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return authorities;
			}

			@Override
			public Object getCredentials() {
				return null;
			}

			@Override
			public Object getDetails() {
				return null;
			}

			@Override
			public Object getPrincipal() {
				return username;
			}

			@Override
			public boolean isAuthenticated() {
				return true;
			}

			@Override
			public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
			}

			@Override
			public String getName() {
				return username;
			}
		};

		// Mock JwtEncoder
		Jwt encodedToken = Mockito.mock(Jwt.class);
		Mockito.when(jwtEncoder.encode(Mockito.any())).thenReturn(encodedToken);

		String jwt = tokenService.generateJwt(authentication);

		Mockito.verify(jwtEncoder).encode(Mockito.any());

		// Perform assertions
		Assertions.assertEquals(encodedToken.getTokenValue(), jwt);
	}
}
