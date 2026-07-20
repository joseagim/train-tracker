package com.joseagim.traintracker.repository;

import com.joseagim.traintracker.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
