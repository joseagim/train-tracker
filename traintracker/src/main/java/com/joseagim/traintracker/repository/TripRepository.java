package com.joseagim.traintracker.repository;

import com.joseagim.traintracker.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByDepartureTimeBetween(LocalDateTime first, LocalDateTime last);

}
