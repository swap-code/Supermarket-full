package com.nagarro.supermarket.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.supermarket.model.User;

/**
 * @author rishabhgusain
 *
 */

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	Optional<User> findUserByUsername(String username);

	Optional<Integer> findUserIdByUsername(String username);

	Optional<User> findByUserId(int userId);
}
