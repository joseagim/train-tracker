package com.joseagim.traintracker.repository;

import com.joseagim.traintracker.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
}
