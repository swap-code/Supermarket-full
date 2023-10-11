package com.nagarro.supermarket.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author rishabhgusain
 *
 */

public class ResponseHandler {

	public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
		Map<String, Object> map = new HashMap<>();
		map.put("message", message);
		map.put("status", status);
		map.put("data", responseObj);
		return new ResponseEntity<>(map, status);
	}

	public static ResponseEntity<Object> generateResponse(String message, HttpStatus status) {
		Map<String, Object> map = new HashMap<>();
		map.put("message", message);
		map.put("status", status);
		return new ResponseEntity<>(map, status);
	}
}
