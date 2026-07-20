package com.joseagim.traintracker.repository;

import com.joseagim.traintracker.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
}
