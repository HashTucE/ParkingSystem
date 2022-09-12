package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSpotDAOTest {

    private static ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
    private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    @BeforeEach
    public void setUpPerTest() {
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService.clearDataBaseEntries();
    }


    @Test
    @DisplayName("Should return the next available slot when there is a slot available")
    void getNextAvailableSlotWhenThereIsASlotAvailable() {
        try {
            int nextAvailableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

            assertEquals(1, nextAvailableSlot);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    @DisplayName("Should return 0 when there is no car slot available")
    void getNextAvailableSlotWhenThereIsNoSlotAvailable() {

        ParkingSpot ps1 = new ParkingSpot(1, ParkingType.CAR, false);
        parkingSpotDAO.updateParking(ps1);
        ParkingSpot ps2 = new ParkingSpot(2, ParkingType.CAR, false);
        parkingSpotDAO.updateParking(ps2);
        ParkingSpot ps3 = new ParkingSpot(3, ParkingType.CAR, false);
        parkingSpotDAO.updateParking(ps3);


        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("Should return true when the parking spot is updated")
    void updateParkingWhenParkingSpotIsUpdatedThenReturnTrue() {

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when the parking spot is not updated")
    void updateParkingWhenParkingSpotIsNotUpdatedThenReturnFalse() {

        ParkingSpot parkingSpotUnknown = new ParkingSpot(0, ParkingType.CAR, false);
        boolean result = parkingSpotDAO.updateParking(parkingSpotUnknown);

        assertFalse(result);
    }
}