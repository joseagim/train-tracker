package com.joseagim.traintracker.dto.response;

import com.joseagim.traintracker.entity.Ticket;

public record TicketValidationResponseDto(
        TicketResponseDto ticket,
        String firstName,
        String lastName,
        String dni,
        boolean scanned
) {

    public static TicketValidationResponseDto from(Ticket ticket) {
        return new TicketValidationResponseDto(
                TicketResponseDto.from(ticket),
                ticket.getUser().getFirstName(),
                ticket.getUser().getLastName(),
                ticket.getUser().getDni(),
                ticket.isScanned()
        );
    }

}
