package com.nagarro.supermarket.service.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nagarro.supermarket.dao.UserDao;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.User;
import com.nagarro.supermarket.service.UserService;

/**
 * @author prernakumari
 * @author rishabhgusain
 *
 */

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private EntityManager entityManager;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userDao.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
	}

	@Override
	public void activateUser(int userId) {
		Optional<User> optionalUser = userDao.findById(userId);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setActive(true);
			userDao.save(user);
		} else {
			throw new RuntimeException("user not found");
		}
	}

	@Override
	public void inactivateUser(int userId) {
		Optional<User> optionalUser = userDao.findById(userId);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setActive(false);
			userDao.save(user);
		} else {
			throw new RuntimeException("user not found");
		}
	}

	@Override
	public List<User> getAllUsers() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.select(root).where(cb.notEqual(root.get("username"), "admin"));
		TypedQuery<User> typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList();
	}

	@Override
	public User getUserById(int userId) {
		User user = userDao.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
		return user;
	}

	@Override
	public User findUserByUsername(String username) {
		Optional<User> userOptional = userDao.findUserByUsername(username);
		User user = userOptional
				.orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
		return user;
	}
}
