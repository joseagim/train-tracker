package com.joseagim.traintracker.service;

import com.joseagim.traintracker.dto.response.TripSearchResponseDto;
import com.joseagim.traintracker.entity.Route;
import com.joseagim.traintracker.entity.RouteStation;
import com.joseagim.traintracker.entity.Trip;
import com.joseagim.traintracker.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripSearchService {

    private final TripRepository tripRepository;

    public TripSearchService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<TripSearchResponseDto> searchTrips(Long from, Long to, LocalDate date, int passengers) {
        return findValidTrips(from, to, date, passengers).stream()
                .map(TripSearchResponseDto::from)
                .collect(Collectors.toList());
    }

    List<Trip> findValidTrips(Long from, Long to, LocalDate date, int passengers) {

        List<Trip> tripsOnDate = tripRepository.findByDepartureTimeBetween(
                date.atStartOfDay(), date.atStartOfDay().plusDays(1).minusSeconds(1));

        return tripsOnDate.stream()
                .filter(trip -> isValid(trip, from, to, passengers))
                .sorted(Comparator.comparing(Trip::getDepartureTime))
                .collect(Collectors.toList());

    }

    boolean isValid(Trip trip, Long from, Long to, int passengers) {
        return isValidRouteOrder(trip.getRoute(), from, to) && hasEnoughSeats(trip.getSeats(), passengers);
    }

    boolean isValidRouteOrder(Route route, Long from, Long to) {

        boolean isFromChecked = false;

        for (RouteStation rs : route.getRouteStations()) {
            // found from station
            if (rs.getStation().getId().equals(from)) {
                isFromChecked = true;
            }
            // found to station
            else if (rs.getStation().getId().equals(to)) {
                return isFromChecked;
            }
        }

        // from and/or to stations not found
        return false;

    }

    boolean hasEnoughSeats(String seats, int passengers) {

        int freeSeats = 0;

        for (int i = 0; i < seats.length(); i++) {
            if (seats.charAt(i) == '1') freeSeats++;
            if (freeSeats >= passengers) return true;
        }

        return false;

    }

}
