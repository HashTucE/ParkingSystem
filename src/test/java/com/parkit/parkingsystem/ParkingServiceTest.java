package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtilMock;
    @Mock
    private static ParkingSpotDAO parkingSpotDAOMock;
    @Mock
    private static TicketDAO ticketDAOMock;


    @Test
    @DisplayName("Should return a parking spot when there is an available slot")
    void getNextParkingNumberIfAvailableWhenThereIsAnAvailableSlot() {
        when(inputReaderUtilMock.readSelection()).thenReturn(1);
        when(parkingSpotDAOMock.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        parkingService = new ParkingService(inputReaderUtilMock, parkingSpotDAOMock, ticketDAOMock);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertNotNull(parkingSpot);
        assertEquals(1, parkingSpot.getId());
    }

    @Test
    @DisplayName("Should saves the ticket when the parking spot is available")
    void processIncomingVehicleWhenParkingSpotIsAvailable() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        parkingService = new ParkingService(inputReaderUtilMock, parkingSpotDAOMock, ticketDAOMock);

        when(inputReaderUtilMock.readSelection()).thenReturn(1);
        when(inputReaderUtilMock.readVehicleRegistrationNumber()).thenReturn("ANYREG");
        when(parkingSpotDAOMock.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(parkingSpotDAOMock.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAOMock).updateParking(parkingSpot);
        verify(ticketDAOMock).saveTicket(any());
    }

    @Test
    @DisplayName("Should update the ticket when the vehicle is found")
    void processExitingVehicleWhenVehicleIsFoundThenUpdateTicket() throws Exception {
        parkingService = new ParkingService(inputReaderUtilMock, parkingSpotDAOMock, ticketDAOMock);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        when(inputReaderUtilMock.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAOMock.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAOMock.updateTicket(any())).thenReturn(true);
        when(parkingSpotDAOMock.updateParking(any())).thenReturn(true);

        parkingService.processExitingVehicle();

        assertNotNull(ticket.getOutTime());
        assertTrue(ticket.getPrice() > 0);
        verify(inputReaderUtilMock, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAOMock, times(1)).getTicket(anyString());
        verify(ticketDAOMock, times(1)).updateTicket(any());
        verify(parkingSpotDAOMock, times(1)).updateParking(any());
    }
}