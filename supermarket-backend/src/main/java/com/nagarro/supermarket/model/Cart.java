package com.nagarro.supermarket.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Aakanksha Class name: Cart Description: It contains all the Cart
 *         attributes
 *
 **/

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cartId;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "totalCart_amount")
	private BigDecimal totalCartAmount = BigDecimal.ZERO;

	@JsonIgnoreProperties("cart")
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
	private List<CartItem> items = new ArrayList<>();
}
