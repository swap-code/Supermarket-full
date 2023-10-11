package com.nagarro.supermarket.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author swapnil
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmiDto {

	private int tenure;
	private BigDecimal roi;
	private BigDecimal monthlyInstallment;
}
