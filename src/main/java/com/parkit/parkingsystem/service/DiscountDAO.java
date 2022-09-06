package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class DiscountDAO {

    private TicketDAO ticketDAO = new TicketDAO();

    public DiscountDAO() {
    }

    public DiscountDAO(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public boolean discount(String incomingRegistration) {

        boolean result = false;

        Ticket ticket = ticketDAO.oldTicket(incomingRegistration);

        if (ticket != null) {
            result = true;
        }

        return result;
    }
}
