package com.nagarro.supermarket.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.supermarket.model.Emi;
import com.nagarro.supermarket.service.EmiService;
import com.nagarro.supermarket.utils.ResponseHandler;

/**
 * 
 * @author swapnil
 * 
 *         Class to get Emi by its ID
 *
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/emi")
public class EmiController {

	private final EmiService emiService;
	private static final Logger logger = Logger.getLogger(EmiController.class);


	@Autowired
	public EmiController(EmiService emiService) {
		this.emiService = emiService;
	}

	@GetMapping("/{emiId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<Object> getEmiById(@PathVariable("emiId") int emiId) {
		logger.info("Fetching EMI by ID: " + emiId);
		Emi emi = emiService.getEmi(emiId);
		if (emi != null) {
			logger.info("EMI fetched successfully.");
			return ResponseHandler.generateResponse("EMI fetched successfully", HttpStatus.OK, emi);
		} else {
			logger.warn("EMI not found with ID: " + emiId);
			return ResponseHandler.generateResponse("EMI is null", HttpStatus.NOT_FOUND);
		}
	}
}
