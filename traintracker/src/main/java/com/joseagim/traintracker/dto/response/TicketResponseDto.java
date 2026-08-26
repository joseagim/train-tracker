package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.Ticket;

public record TicketResponseDto(
        Long id,
        String uuid,
        TripResponseDto trip,
        StationResponseDto origin,
        StationResponseDto destination,
        int bogey,
        int seat,
        double price
) {

    public static TicketResponseDto from(Ticket ticket) {
        return new TicketResponseDto(
                ticket.getId(),
                ticket.getUuid(),
                TripResponseDto.from(ticket.getTrip()),
                StationResponseDto.from(ticket.getOrigin()),
                StationResponseDto.from(ticket.getDestination()),
                ticket.getBogey(),
                ticket.getSeat(),
                ticket.getPrice()
        );
    }

}
