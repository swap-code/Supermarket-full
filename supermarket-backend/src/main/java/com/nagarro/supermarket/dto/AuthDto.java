package com.nagarro.supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rishabhgusain
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthDto {

	private String username;
	private String password;

	public String toString() {
		return "Registration info: username: " + this.username + " password: " + this.password;
	}
}