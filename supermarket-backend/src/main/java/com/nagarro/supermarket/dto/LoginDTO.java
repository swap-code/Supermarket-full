package com.nagarro.supermarket.dto;

/**
 * @author rishabhgusain
 *
 */

public class LoginDTO {

	private String jwt;

	public LoginDTO(String jwt) {
		this.jwt = jwt;
	}

	public String getJwt() {
		return this.jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}