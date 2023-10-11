package com.nagarro.supermarket.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.supermarket.model.Role;

/**
 * @author rishabhgusain
 *
 */

@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {
	
	Optional<Role> findByAuthority(String authority);
}