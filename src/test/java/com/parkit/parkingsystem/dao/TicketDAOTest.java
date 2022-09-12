package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TicketDAOTest {

    private static final String REG = "ANYREG";

    private final static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    private final static TicketDAO ticketDAO = new TicketDAO();

    @BeforeAll
    public static void setUp() {
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @Test
    @DisplayName("Should return false when the ticket is saved successfully")
    void saveTicketWhenTicketIsSavedSuccessfullyThenReturnFalse() {
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(REG);
        ticket.setPrice(10);
        ticket.setInTime(new Date());

        assertFalse(ticketDAO.saveTicket(ticket));

    }

    @Test
    @DisplayName("Should assert not null when a ticket already exist")
    void getTicketWhenTicketAlreadyExistThenParametersNotNull() {
        Ticket result = ticketDAO.getTicket(REG);

        assertNotNull(result);
        assertNotNull(result.getParkingSpot());
        assertNotNull(result.getInTime());

    }

    @Test
    @DisplayName("Should update the ticket when the price change")
    void updateTicketWhenPriceChange() {
        Ticket result = ticketDAO.getTicket(REG);
        result.setPrice(10.0);
        result.setOutTime(new Date());
        ticketDAO.updateTicket(result);
        result = ticketDAO.getTicket(REG);

        assertEquals(10.0, result.getPrice());
    }


}