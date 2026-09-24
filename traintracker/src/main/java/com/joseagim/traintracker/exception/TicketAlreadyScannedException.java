package com.joseagim.traintracker.exception;

public class TicketAlreadyScannedException extends RuntimeException {
    public TicketAlreadyScannedException(String message) {
        super(message);
    }
}
