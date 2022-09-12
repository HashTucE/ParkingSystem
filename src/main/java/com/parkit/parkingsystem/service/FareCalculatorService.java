package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

public class FareCalculatorService {

    private DiscountDAO discountDAO;

    public void setDiscountDAO(DiscountDAO discountDAO) {
        this.discountDAO = discountDAO;
    }

    public FareCalculatorService() {
        this.discountDAO = new DiscountDAO();
    }
    public void calculateFare(Ticket ticket) throws Exception {

        boolean result;

        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        Duration duration = Duration.between(ticket.getInTime().toInstant(), ticket.getOutTime().toInstant());
        float durationToMinutes = duration.toMinutes();
        float durationToHours = durationToMinutes / 60;

        if (durationToMinutes <= 30) {
            ticket.setPrice(Fare.FREE_PARKING);
            System.out.println("Free parking under 30 minutes!");
        } else {

            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(durationToHours * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(durationToHours * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
            result = discountDAO.discount(ticket.getVehicleRegNumber());

            if (result) {
                ticket.setPrice(ticket.getPrice() * 0.95);
                System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
            }
            }
        }
    }