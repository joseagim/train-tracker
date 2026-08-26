package com.joseagim.traintracker.service;

import com.joseagim.traintracker.dto.request.TicketRequestDto;
import com.joseagim.traintracker.dto.response.TicketResponseDto;
import com.joseagim.traintracker.entity.Station;
import com.joseagim.traintracker.entity.Ticket;
import com.joseagim.traintracker.entity.Trip;
import com.joseagim.traintracker.entity.User;
import com.joseagim.traintracker.exception.NoSeatsAvailableException;
import com.joseagim.traintracker.exception.ResourceNotFoundException;
import com.joseagim.traintracker.repository.StationRepository;
import com.joseagim.traintracker.repository.TicketRepository;
import com.joseagim.traintracker.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    private final TripRepository tripRepository;

    private final StationRepository stationRepository;

    public TicketService(
            TicketRepository ticketRepository, TripRepository tripRepository, StationRepository stationRepository) {
        this.ticketRepository = ticketRepository;
        this.tripRepository = tripRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public TicketResponseDto purchase(User user, TicketRequestDto ticketRequest) {

        // validate request data

        Trip trip = tripRepository.findById(ticketRequest.tripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + ticketRequest.tripId()));

        Station origin = stationRepository.findById(ticketRequest.origin())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + ticketRequest.origin()));

        Station destination = stationRepository.findById(ticketRequest.destination())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + ticketRequest.destination()));

        // check free seat

        int seatIndex = findFirstFreeSeat(trip.getSeats());
        if (seatIndex == -1)
            throw new NoSeatsAvailableException("No seats available for trip with id: " + trip.getId());

        // update trip seats

        String updatedSeats = trip.getSeats().substring(0, seatIndex) + "0" + trip.getSeats().substring(seatIndex + 1);
        trip.setSeats(updatedSeats);
        tripRepository.save(trip);

        // create ticket

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setTrip(trip);
        ticket.setOrigin(origin);
        ticket.setDestination(destination);
        ticket.setBogey(seatIndex / trip.getTrain().getSeatsByBogey() + 1);
        ticket.setSeat(seatIndex % trip.getTrain().getSeatsByBogey() + 1);
        ticket.setPrice(trip.getRoute().minutesBetween(origin.getId(), destination.getId()) * 0.15);

        Ticket saved = ticketRepository.save(ticket);

        return TicketResponseDto.from(saved);

    }

    private int findFirstFreeSeat(String seats) {
        return seats.indexOf("1");
    }

}
