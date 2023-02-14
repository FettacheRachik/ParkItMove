package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
	private static final String numberOfVehicle="ABCDEF";
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static final Logger logger = LogManager.getLogger("App");

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(numberOfVehicle);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        
    	//given
    	int slotToGive =parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        //when
        parkingService.processIncomingVehicle();
        
        //then
        //check that a ticket is actualy saved in DB
        Ticket ticketSaved = ticketDAO.getTicket(numberOfVehicle);
        assertNotNull(ticketSaved);
        assertFalse(ticketSaved.getParkingSpot().isAvailable());
       
        //and Parking table is updated with availability
        int slotNext =parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(slotToGive,slotNext);
        
        
    }

    @Test
    public void testParkingLotExit(){
    	//Given
        testParkingACar();
        Ticket ticketIntime = ticketDAO.getTicket(numberOfVehicle);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        long outTime = ticketIntime.getInTime().getTime()+ 60*60*1000;
        //For reccuring
        boolean isReccurring=ticketDAO.isUserRecurring(numberOfVehicle);
        //when 
        parkingService.processExitingVehicle(new Date(outTime));
        
        
        Ticket ticket = ticketDAO.getTicket(numberOfVehicle);
        
        System.out.println("recurring " + isReccurring);
                
       // check that the fare generated and out time are populated correctly in the database
        assertNotNull(ticket);
        assertNotNull(ticket.getOutTime());
        assertEquals(isReccurring ?1.425:1.5,Math.round(ticket.getPrice() * 1000.0)/1000.0);

        assertEquals(outTime,ticket.getOutTime().getTime());

        
        
    
    }
    
    @Test
    public void testCarExitRecurringUser() {
    	testParkingLotExit();
    	testParkingLotExit();
    }

}
