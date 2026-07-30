package com.joseagim.traintracker.service;

import com.joseagim.traintracker.entity.Route;
import com.joseagim.traintracker.entity.RouteStation;
import com.joseagim.traintracker.entity.Trip;
import com.joseagim.traintracker.repository.TripRepository;
import org.springframework.stereotype.Service;

@Service
public class TripSearchService {

    private final TripRepository tripRepository;

    public TripSearchService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
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
