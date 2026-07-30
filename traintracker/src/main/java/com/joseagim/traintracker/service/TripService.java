package com.joseagim.traintracker.service;

import com.joseagim.traintracker.dto.request.TripRequestDto;
import com.joseagim.traintracker.dto.response.TripResponseDto;
import com.joseagim.traintracker.entity.Route;
import com.joseagim.traintracker.entity.RouteStation;
import com.joseagim.traintracker.entity.Train;
import com.joseagim.traintracker.entity.Trip;
import com.joseagim.traintracker.exception.ResourceNotFoundException;
import com.joseagim.traintracker.repository.RouteRepository;
import com.joseagim.traintracker.repository.TrainRepository;
import com.joseagim.traintracker.repository.TripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;

    public TripService(
            TripRepository tripRepository,
            RouteRepository routeRepository,
            TrainRepository trainRepository) {

        this.tripRepository = tripRepository;
        this.routeRepository = routeRepository;
        this.trainRepository = trainRepository;
    }

    public TripResponseDto create(TripRequestDto tripRequest) {

        Route route = routeRepository.findById(tripRequest.routeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route not found with id: " + tripRequest.routeId()));

        Train train = trainRepository.findById(tripRequest.trainId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Train not found with id: " + tripRequest.trainId()));

        Trip trip = new Trip();
        trip.setRoute(route);
        trip.setTrain(train);
        trip.setDepartureTime(tripRequest.departureTime());
        trip.setSeats("1".repeat(train.getTotalSeats()));

        Trip saved = tripRepository.save(trip);

        return TripResponseDto.from(saved);

    }

    public TripResponseDto findById(Long id) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + id));

        return TripResponseDto.from(trip);
    }

    public Page<TripResponseDto> findAll(Pageable pageable) {
        return tripRepository.findAll(pageable).map(TripResponseDto::from);
    }

    public TripResponseDto update(Long id, TripRequestDto tripRequest) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + id));

        Route route = routeRepository.findById(tripRequest.routeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route not found with id: " + tripRequest.routeId()));

        Train train = trainRepository.findById(tripRequest.trainId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Train not found with id: " + tripRequest.trainId()));

        trip.setRoute(route);
        trip.setTrain(train);
        trip.setDepartureTime(tripRequest.departureTime());
        trip.setSeats("1".repeat(train.getTotalSeats()));

        // when implementing Ticket and User
        // - check new train has >= seats than before
        // - update User's tickets from this trip

        Trip saved = tripRepository.save(trip);

        return TripResponseDto.from(saved);

    }

    public void delete(Long id) {
        Trip trip = tripRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + id));
        tripRepository.delete(trip);
    }

}
