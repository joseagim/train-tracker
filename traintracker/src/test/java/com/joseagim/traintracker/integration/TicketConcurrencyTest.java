package com.joseagim.traintracker.integration;

import com.joseagim.traintracker.dto.request.TicketRequestDto;
import com.joseagim.traintracker.entity.*;
import com.joseagim.traintracker.exception.NoSeatsAvailableException;
import com.joseagim.traintracker.repository.*;
import com.joseagim.traintracker.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TicketConcurrencyTest {

    @Autowired
    TicketService ticketService;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    TripRepository tripRepository;

    @Test
    void purchase_onlyOneTicket_whenMultipleThreadsCompeteForSameSeat()
            throws InterruptedException {

        Station s1 = new Station();
        s1.setName("s1");
        s1.setCity("c1");
        s1 = stationRepository.save(s1);

        Station s2 = new Station();
        s2.setName("s2");
        s2.setCity("c2");
        s2 = stationRepository.save(s2);

        RouteStation rs1 = new RouteStation();
        rs1.setStation(s1);
        rs1.setStopOrder(1);
        rs1.setMinutesFromStart(0);


        RouteStation rs2 = new RouteStation();
        rs2.setStation(s2);
        rs2.setStopOrder(2);
        rs2.setMinutesFromStart(20);

        Route r = new Route();
        r.setName("r");
        r.addRouteStation(rs1);
        r.addRouteStation(rs2);
        r = routeRepository.save(r);

        Train t = new Train();
        t.setType("t");
        t.setBogeys(1);
        t.setSeatsByBogey(1);
        t = trainRepository.save(t);

        Trip trip = new Trip();
        trip.setRoute(r);
        trip.setTrain(t);
        trip.setSeats("1");
        trip.setDepartureTime(LocalDateTime.of(2026, 9, 1, 18, 0));
        trip = tripRepository.save(trip);

        User u1 = new User();
        u1.setFirstName("User");
        u1.setLastName("1");
        u1.setEmail("user1@test.com");
        u1.setPhoneNumber("111111111");
        u1.setDni("11111111A");
        u1.setPassword("aaaaaaaa");
        u1.setRole(UserRole.ROLE_USER);
        User buy1 = userRepository.save(u1);

        User u2 = new User();
        u2.setFirstName("User");
        u2.setLastName("2");
        u2.setEmail("user2@test.com");
        u2.setPhoneNumber("222222222");
        u2.setDni("22222222B");
        u2.setPassword("bbbbbbbb");
        u2.setRole(UserRole.ROLE_USER);
        User buy2 = userRepository.save(u2);

        TicketRequestDto request = new TicketRequestDto(
                trip.getId(), s1.getId(), s2.getId());

        final int[] successCount = {0};
        final int[] failureCount = {0};

        try {
            Thread thread1 = new Thread(() -> {
                try {
                    ticketService.purchase(buy1, request);
                    successCount[0]++;
                } catch (NoSeatsAvailableException | OptimisticLockingFailureException e) {
                    failureCount[0]++;
                }
            });

            Thread thread2 = new Thread(() -> {
                try {
                    ticketService.purchase(buy2, request);
                    successCount[0]++;
                } catch (NoSeatsAvailableException | OptimisticLockingFailureException e) {
                    failureCount[0]++;
                }
            });

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();

        } finally {
            // always delete "trash" data from bbdd
            ticketRepository.deleteAll(ticketRepository.findByUser(buy1));
            ticketRepository.deleteAll(ticketRepository.findByUser(buy2));

            tripRepository.findById(trip.getId())
                    .ifPresent(freshTrip -> tripRepository.delete(freshTrip));

            trainRepository.delete(t);
            routeRepository.delete(r);
            stationRepository.delete(s1);
            stationRepository.delete(s2);
            userRepository.delete(buy1);
            userRepository.delete(buy2);
        }

        assertEquals(1, successCount[0]);
        assertEquals(1, failureCount[0]);

    }


}
