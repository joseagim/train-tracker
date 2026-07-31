package com.joseagim.traintracker.service;

import com.joseagim.traintracker.entity.Route;
import com.joseagim.traintracker.entity.RouteStation;
import com.joseagim.traintracker.entity.Station;
import com.joseagim.traintracker.entity.Trip;
import com.joseagim.traintracker.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripSearchServiceTest {

    @InjectMocks
    private TripSearchService tripSearchService;

    @Mock
    private TripRepository tripRepository;



    // ==================== findValidTrips ====================

    @Test
    void findValidTrips_filtersOutInvalidTrip_whenListHasValidAndInvalid() {

        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("s1");
        s1.setCity("c1");

        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("s2");
        s2.setCity("c2");

        RouteStation rs1 = new RouteStation();
        rs1.setId(1L);
        rs1.setStation(s1);
        rs1.setStopOrder(1);
        rs1.setMinutesFromStart(0);

        RouteStation rs2 = new RouteStation();
        rs2.setId(2L);
        rs2.setStation(s2);
        rs2.setStopOrder(2);
        rs2.setMinutesFromStart(20);

        Route r1 = new Route();
        r1.setName("r1");
        r1.addRouteStation(rs1);
        r1.addRouteStation(rs2);

        Trip t1 = new Trip();
        t1.setId(1L);
        t1.setRoute(r1);
        t1.setSeats("01001");
        t1.setDepartureTime(LocalDateTime.of(2026, 7, 31, 14, 0));

        Trip t2 = new Trip();
        t2.setId(2L);
        t2.setRoute(r1);
        t2.setSeats("01111");
        t2.setDepartureTime(LocalDateTime.of(2026, 7, 31, 9, 0));

        when(tripRepository.findByDepartureTimeBetween(any(),any()))
                .thenReturn(List.of(t1, t2));

        List<Trip> result = tripSearchService.findValidTrips(
                1L, 2L, LocalDate.of(2026, 7, 31), 4);

        assertEquals(List.of(t2), result);

    }

    @Test
    void findValidTrips_returnsSortedByDepartureTime() {

        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("s1");
        s1.setCity("c1");

        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("s2");
        s2.setCity("c2");

        RouteStation rs1 = new RouteStation();
        rs1.setId(1L);
        rs1.setStation(s1);
        rs1.setStopOrder(1);
        rs1.setMinutesFromStart(0);

        RouteStation rs2 = new RouteStation();
        rs2.setId(2L);
        rs2.setStation(s2);
        rs2.setStopOrder(2);
        rs2.setMinutesFromStart(20);

        Route r1 = new Route();
        r1.setName("r1");
        r1.addRouteStation(rs1);
        r1.addRouteStation(rs2);

        Trip t1 = new Trip();
        t1.setId(1L);
        t1.setRoute(r1);
        t1.setSeats("1111");
        t1.setDepartureTime(LocalDateTime.of(2026, 7, 31, 14, 1));

        Trip t2 = new Trip();
        t2.setId(2L);
        t2.setRoute(r1);
        t2.setSeats("1111");
        t2.setDepartureTime(LocalDateTime.of(2026, 7, 31, 14, 0));

        when(tripRepository.findByDepartureTimeBetween(any(), any()))
                .thenReturn(List.of(t1, t2));

        List<Trip> result = tripSearchService.findValidTrips(
                1L, 2L, LocalDate.of(2026, 7, 31), 2);

        assertEquals(List.of(t2, t1), result);

    }

    @Test
    void findValidTrips_returnsEmptyList_whenListHasNoTrips() {

        when(tripRepository.findByDepartureTimeBetween(any(), any()))
                .thenReturn(List.of());

        List<Trip> result = tripSearchService.findValidTrips(
                1L, 2L, LocalDate.of(2026, 7, 31), 2);

        assertEquals(List.of(), result);

    }


    // ==================== isValidRouteOrder ====================

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

        boolean result = tripSearchService.isValidRouteOrder(r, s1.getId(), s3.getId());

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

        boolean result = tripSearchService.isValidRouteOrder(r, s3.getId(), s1.getId());

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

        boolean result = tripSearchService.isValidRouteOrder(r, s4.getId(), s3.getId());

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

        boolean result = tripSearchService.isValidRouteOrder(r, s1.getId(), s4.getId());

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

        boolean result = tripSearchService.isValidRouteOrder(r, s4.getId(), s5.getId());

        assertFalse(result);
    }

    // ==================== hasEnoughSeats ====================

    @Test
    void hasEnoughSeats_valid_moreFreeSeatsThanPassengers() {

        String seats = "10010111";
        int passengers = 3;

        boolean result = tripSearchService.hasEnoughSeats(seats, passengers);

        assertTrue(result);

    }

    @Test
    void hasEnoughSeats_valid_sameFreeSeatsThanPassengers() {

        String seats = "01010011";
        int passengers = 4;

        boolean result = tripSearchService.hasEnoughSeats(seats, passengers);

        assertTrue(result);

    }

    @Test
    void hasEnoughSeats_notValid_morePassengersThanFreeSeats() {

        String seats = "10011";
        int passengers = 4;

        boolean result = tripSearchService.hasEnoughSeats(seats, passengers);

        assertFalse(result);

    }

    @Test
    void hasEnoughSeats_notValid_morePassengersThanSeats() {

        String seats = "10";
        int passengers = 4;

        boolean result = tripSearchService.hasEnoughSeats(seats, passengers);

        assertFalse(result);

    }



}
