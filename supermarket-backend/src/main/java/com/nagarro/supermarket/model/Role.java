package com.nagarro.supermarket.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.NoArgsConstructor;

/**
 * @author rishabhgusain
 *
 */
@Entity
@NoArgsConstructor
@Table(name = "roles")
public class Role implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "role_id")
	private int roleId;

	private String authority;

	public Role(String authority) {
		super();
		this.authority = authority;
	}

	public Role(int roleId, String authority) {
		super();
		this.roleId = roleId;
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}
}
