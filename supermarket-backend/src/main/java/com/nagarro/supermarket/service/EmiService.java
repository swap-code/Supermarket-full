package com.nagarro.supermarket.service;

import com.nagarro.supermarket.dto.EmiDto;
import com.nagarro.supermarket.model.Emi;

/**
 * 
 * @author swapnil
 *
 */

public interface EmiService {

	void addEmi(EmiDto emiDto);

	Emi getEmi(int emiId);
}
