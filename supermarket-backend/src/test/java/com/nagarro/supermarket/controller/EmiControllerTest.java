package com.nagarro.supermarket.controller;

import com.nagarro.supermarket.model.Emi;
import com.nagarro.supermarket.service.EmiService;
import com.nagarro.supermarket.utils.ResponseHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.anyInt;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rishabhgusain
 *
 */
public class EmiControllerTest {

    @Mock
    private EmiService emiService;

    @InjectMocks
    private EmiController emiController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetEmiById_ValidEmiId_ReturnEmi() {
        // Mock input
        int emiId = 1;
        Emi expectedEmi = new Emi(); // Create a sample Emi object

        // Mock dependencies
        Mockito.when(emiService.getEmi(emiId)).thenReturn(expectedEmi);

        ResponseEntity<Object> response = emiController.getEmiById(emiId);

        Mockito.verify(emiService).getEmi(emiId);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

        Object responseBody = response.getBody();
        Assertions.assertTrue(responseBody instanceof HashMap);

        HashMap<String, Object> responseMap = (HashMap<String, Object>) responseBody;
        Assertions.assertTrue(responseMap.containsKey("data"));

        Emi actualEmi = (Emi) responseMap.get("data");

        Assertions.assertEquals(expectedEmi, actualEmi);
    }
    
    
    @Test
    public void testGetEmiById_InvalidEmiId_ReturnNotFound() {
        // Mock input
        int emiId = 1;

        // Mock dependencies
        Mockito.when(emiService.getEmi(emiId)).thenReturn(null);

        ResponseEntity<Object> response = emiController.getEmiById(emiId);

        Mockito.verify(emiService).getEmi(emiId);
        Mockito.verifyNoMoreInteractions(emiService);

        // Perform assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Response status should be 404 NOT_FOUND");
        Assertions.assertNotNull(response.getBody(), "Response body should not be null");
        Assertions.assertEquals("EMI is null", ((Map) response.getBody()).get("message"), "Response body message should be 'EMI is null'");
    }





}





