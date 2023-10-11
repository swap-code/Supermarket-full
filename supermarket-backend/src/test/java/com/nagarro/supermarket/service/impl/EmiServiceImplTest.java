package com.nagarro.supermarket.service.impl;

import com.nagarro.supermarket.dao.EmiDao;
import com.nagarro.supermarket.dto.EmiDto;
import com.nagarro.supermarket.exceptions.ResourceNotFoundException;
import com.nagarro.supermarket.model.Emi;
import com.nagarro.supermarket.service.EmiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Optional;

/**
 * @author rishabhgusain
 *
 */
public class EmiServiceImplTest {

    @Mock
    private EmiDao emiDao;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmiServiceImpl emiService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddEmi() {
        // Mock input
        EmiDto emiDto = new EmiDto();
        Emi emi = new Emi();

        // Mock dependencies
        Mockito.when(modelMapper.map(emiDto, Emi.class)).thenReturn(emi);

        emiService.addEmi(emiDto);

        Mockito.verify(emiDao).save(emi);
    }

    @Test
    public void testGetEmi_ExistingEmiId_ReturnsEmi() {
        // Mock input
        int emiId = 1;
        Emi emi = new Emi();

        // Mock dependencies
        Mockito.when(emiDao.findById(emiId)).thenReturn(Optional.of(emi));

        // Call the method to test
        Emi result = emiService.getEmi(emiId);

        // Verify that the findById method was called on the emiDao with the correct emiId
        Mockito.verify(emiDao).findById(emiId);

        // Perform assertions
        Assertions.assertEquals(emi, result);
    }

    @Test
    public void testGetEmi_NonExistingEmiId_ThrowsResourceNotFoundException() {
        // Mock input
        int emiId = 1;

        // Mock dependencies
        Mockito.when(emiDao.findById(emiId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> emiService.getEmi(emiId));


        Mockito.verify(emiDao).findById(emiId);
    }
}
