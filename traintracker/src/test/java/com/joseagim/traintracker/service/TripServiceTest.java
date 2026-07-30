package com.joseagim.traintracker.service;

import com.joseagim.traintracker.entity.Route;
import com.joseagim.traintracker.entity.RouteStation;
import com.joseagim.traintracker.entity.Station;
import com.joseagim.traintracker.repository.RouteRepository;
import com.joseagim.traintracker.repository.TrainRepository;
import com.joseagim.traintracker.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @InjectMocks
    private TripService tripService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private TrainRepository trainRepository;

    @Test
    void isValidRouteOrder_valid_fromBeforeTo() {

        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("Madrid");
        s1.setCity("Madrid");

        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("Zaragoza");
        s2.setCity("Zaragoza");

        Station s3 = new Station();
        s3.setId(3L);
        s3.setName("Barcelona");
        s3.setCity("Barcelona");

        Route r = new Route();
        r.setName("Madrid-Barcelona");

        RouteStation rs1 = new RouteStation();
        rs1.setStation(s1);
        rs1.setStopOrder(1);
        rs1.setMinutesFromStart(0);
        r.addRouteStation(rs1);

        RouteStation rs2 = new RouteStation();
        rs2.setStation(s2);
        rs2.setStopOrder(2);
        rs2.setMinutesFromStart(5);
        r.addRouteStation(rs2);

        RouteStation rs3 = new RouteStation();
        rs3.setStation(s3);
        rs3.setStopOrder(3);
        rs3.setMinutesFromStart(10);
        r.addRouteStation(rs3);

        boolean result = tripService.isValidRouteOrder(r, s1.getId(), s3.getId());

        assertTrue(result);

    }

    @Test
    void isValidRouteOrder_notValid_fromAfterTo() {

        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("Madrid");
        s1.setCity("Madrid");

        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("Zaragoza");
        s2.setCity("Zaragoza");

        Station s3 = new Station();
        s3.setId(3L);
        s3.setName("Barcelona");
        s3.setCity("Barcelona");

        Route r = new Route();
        r.setName("Madrid-Barcelona");

        RouteStation rs1 = new RouteStation();
        rs1.setStation(s1);
        rs1.setStopOrder(1);
        rs1.setMinutesFromStart(0);
        r.addRouteStation(rs1);

        RouteStation rs2 = new RouteStation();
        rs2.setStation(s2);
        rs2.setStopOrder(2);
        rs2.setMinutesFromStart(5);
        r.addRouteStation(rs2);

        RouteStation rs3 = new RouteStation();
        rs3.setStation(s3);
        rs3.setStopOrder(3);
        rs3.setMinutesFromStart(10);
        r.addRouteStation(rs3);

        boolean result = tripService.isValidRouteOrder(r, s3.getId(), s1.getId());

        assertFalse(result);

    }

    @Test
    void isValidRouteOrder_notValid_fromDoesNotExist() {

        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("Madrid");
        s1.setCity("Madrid");

        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("Zaragoza");
        s2.setCity("Zaragoza");

        Station s3 = new Station();
        s3.setId(3L);
        s3.setName("Barcelona");
        s3.setCity("Barcelona");

        Station s4 = new Station();
        s4.setId(4L);
        s4.setName("Valencia");
        s4.setCity("Valencia");

        Route r = new Route();
        r.setName("Madrid-Barcelona");

        RouteStation rs1 = new RouteStation();
        rs1.setStation(s1);
        rs1.setStopOrder(1);
        rs1.setMinutesFromStart(0);
        r.addRouteStation(rs1);

        RouteStation rs2 = new RouteStation();
        rs2.setStation(s2);
        rs2.setStopOrder(2);
        rs2.setMinutesFromStart(5);
        r.addRouteStation(rs2);

        RouteStation rs3 = new RouteStation();
        rs3.setStation(s3);
        rs3.setStopOrder(3);
        rs3.setMinutesFromStart(10);
        r.addRouteStation(rs3);

        boolean result = tripService.isValidRouteOrder(r, s4.getId(), s3.getId());

        assertFalse(result);
    }

    @Test
    void isValidRouteOrder_notValid_toDoesNotExist() {

        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("Madrid");
        s1.setCity("Madrid");

        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("Zaragoza");
        s2.setCity("Zaragoza");

        Station s3 = new Station();
        s3.setId(3L);
        s3.setName("Barcelona");
        s3.setCity("Barcelona");

        Station s4 = new Station();
        s4.setId(4L);
        s4.setName("Valencia");
        s4.setCity("Valencia");

        Route r = new Route();
        r.setName("Madrid-Barcelona");

        RouteStation rs1 = new RouteStation();
        rs1.setStation(s1);
        rs1.setStopOrder(1);
        rs1.setMinutesFromStart(0);
        r.addRouteStation(rs1);

        RouteStation rs2 = new RouteStation();
        rs2.setStation(s2);
        rs2.setStopOrder(2);
        rs2.setMinutesFromStart(5);
        r.addRouteStation(rs2);

        RouteStation rs3 = new RouteStation();
        rs3.setStation(s3);
        rs3.setStopOrder(3);
        rs3.setMinutesFromStart(10);
        r.addRouteStation(rs3);

        boolean result = tripService.isValidRouteOrder(r, s1.getId(), s4.getId());

        assertFalse(result);
    }

    @Test
    void isValidRouteOrder_notValid_fromAndToDoNotExist() {

        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("Madrid");
        s1.setCity("Madrid");

        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("Zaragoza");
        s2.setCity("Zaragoza");

        Station s3 = new Station();
        s3.setId(3L);
        s3.setName("Barcelona");
        s3.setCity("Barcelona");

        Station s4 = new Station();
        s4.setId(4L);
        s4.setName("Valencia");
        s4.setCity("Valencia");

        Station s5 = new Station();
        s5.setId(5L);
        s5.setName("Sevilla");
        s5.setCity("Sevilla");

        Route r = new Route();
        r.setName("Madrid-Barcelona");

        RouteStation rs1 = new RouteStation();
        rs1.setStation(s1);
        rs1.setStopOrder(1);
        rs1.setMinutesFromStart(0);
        r.addRouteStation(rs1);

        RouteStation rs2 = new RouteStation();
        rs2.setStation(s2);
        rs2.setStopOrder(2);
        rs2.setMinutesFromStart(5);
        r.addRouteStation(rs2);

        RouteStation rs3 = new RouteStation();
        rs3.setStation(s3);
        rs3.setStopOrder(3);
        rs3.setMinutesFromStart(10);
        r.addRouteStation(rs3);

        boolean result = tripService.isValidRouteOrder(r, s4.getId(), s5.getId());

        assertFalse(result);
    }



    @Test
    void hasEnoughSeats_valid_moreFreeSeatsThanPassengers() {

        String seats = "10010111";
        int passengers = 3;

        boolean result = tripService.hasEnoughSeats(seats, passengers);

        assertTrue(result);

    }

    @Test
    void hasEnoughSeats_valid_sameFreeSeatsThanPassengers() {

        String seats = "01010011";
        int passengers = 4;

        boolean result = tripService.hasEnoughSeats(seats, passengers);

        assertTrue(result);

    }

    @Test
    void hasEnoughSeats_notValid_morePassengersThanFreeSeats() {

        String seats = "10011";
        int passengers = 4;

        boolean result = tripService.hasEnoughSeats(seats, passengers);

        assertFalse(result);

    }

    @Test
    void hasEnoughSeats_notValid_morePassengersThanSeats() {

        String seats = "10";
        int passengers = 4;

        boolean result = tripService.hasEnoughSeats(seats, passengers);

        assertFalse(result);

    }



}
