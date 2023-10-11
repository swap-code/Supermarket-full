package com.nagarro.supermarket.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.supermarket.model.Emi;

/**
 * 
 * @author swapnil
 *
 */

@Repository
public interface EmiDao extends JpaRepository<Emi, Integer> {

}
