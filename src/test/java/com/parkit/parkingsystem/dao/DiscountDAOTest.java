package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.DiscountDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiscountDAOTest {

    @Mock
    private static TicketDAO ticketDAOMock;

    @Test
    @DisplayName("Discount should return true when the ticket already exist")
    void discountWhenTicketAlreadyExistThenReturnTrue() {
        Ticket ticket = new Ticket();
        DiscountDAO dc = new DiscountDAO(ticketDAOMock);

        when(ticketDAOMock.getOldTicket(anyString())).thenReturn(ticket);

        assertTrue(dc.discount("ANYREG"));
        verify(ticketDAOMock, Mockito.times(1)).getOldTicket(anyString());

    }
}
