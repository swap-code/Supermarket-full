package com.nagarro.supermarket.service.impl;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nagarro.supermarket.dao.RoleDao;
import com.nagarro.supermarket.dao.UserDao;
import com.nagarro.supermarket.dto.LoginDTO;
import com.nagarro.supermarket.model.Role;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.AuthenticationService;
import com.nagarro.supermarket.service.TokenService;

/**
 * @author rishabhgusain
 *
 */

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private AuthenticationManager authenticationManager;

	private boolean active = true;

	public User registerUser(String username, String password) {

		String encodedPassword = passwordEncoder.encode(password);
		Role userRole = roleDao.findByAuthority("USER").get();

		Set<Role> authorities = new HashSet<>();

		authorities.add(userRole);

		return userDao.save(new User(0, username, encodedPassword, authorities, active));
	}

	public LoginDTO loginUser(String username, String password) {
		try {
			Authentication auth = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			String token = tokenService.generateJwt(auth);
			return new LoginDTO(token);
		} catch (AuthenticationException e) {
			throw new RuntimeException("User Authentication Failed");
		}
	}
}
