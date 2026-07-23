package com.joseagim.traintracker.exception;

import com.joseagim.traintracker.dto.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDto(
                        e.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        LocalDateTime.now(),
                        request.getRequestURI(),
                        request.getMethod()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleBeanValidationError(MethodArgumentNotValidException e, HttpServletRequest request) {

        StringBuilder sb = new StringBuilder("Not valid values on fields: \n");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            // message format for each bean validation error:
            // key: "value" => error message
            sb.append(fieldError.getField()).append(": ")
                    .append("\"").append(fieldError.getRejectedValue()).append("\"")
                    .append(" => ").append(fieldError.getDefaultMessage()).append("\n");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDto(
                        sb.toString(),
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now(),
                        request.getRequestURI(),
                        request.getMethod()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e, HttpServletRequest request) {
        log.error("Internal error: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseDto(
                        "An internal error has occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        LocalDateTime.now(),
                        request.getRequestURI(),
                        request.getMethod()
                )
        );
    }

}
