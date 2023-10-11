package com.nagarro.supermarket.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.supermarket.dao.EmiDao;
import com.nagarro.supermarket.dto.EmiDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.Emi;
import com.nagarro.supermarket.service.EmiService;

/**
 * 
 * @author swapnil
 *
 */

@Service
public class EmiServiceImpl implements EmiService {

	@Autowired
	private EmiDao emiDao;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public void addEmi(EmiDto emiDto) {
		Emi emi = this.modelMapper.map(emiDto, Emi.class);
		this.emiDao.save(emi);
	}

	@Override
	public Emi getEmi(int emiId) {
		Emi emi = emiDao.findById(emiId).orElseThrow(() -> new ResourceNotFoundException("Emi", "Emi ID", emiId));
		return emi;
	}
}
