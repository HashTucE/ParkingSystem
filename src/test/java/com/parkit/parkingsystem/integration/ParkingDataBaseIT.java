package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static final ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
    private static final TicketDAO ticketDAO = new TicketDAO();

    private Ticket ticket = new Ticket();
    private static final DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeEach
    public void setUpPerTest() throws Exception {
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService.clearDataBaseEntries();

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    }

    @AfterAll
    public static void clearUp() {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    @DisplayName("Check that a ticket is saved in DB and the parking table is updated")
    public void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        // DONE: check that a ticket is actually saved in DB
        ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket);

        // DONE: check that the Parking table is updated with availability
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        int parkingNumber = parkingSpot.getId();
        assertEquals(1, parkingNumber);
        assertFalse(parkingSpot.isAvailable());
    }

    @Test
    @DisplayName("Check that the fare and the timeout are generated correctly in the DB")
    public void testParkingLotExit() throws Exception {
        testParkingACar();

        ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticketDAO.saveTicket(ticket);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        ticket = ticketDAO.getTicket("ABCDEF");

        // DONE: check that the fare are generated correctly in the database
        assertEquals(1.5, (Math.round(ticket.getPrice()*10.0))/10.0);

        // DONE: check that out time are populated correctly in the database
        assertNotNull(ticket.getOutTime());
        assertNotEquals(ticket.getInTime(), ticket.getOutTime());
    }

}
