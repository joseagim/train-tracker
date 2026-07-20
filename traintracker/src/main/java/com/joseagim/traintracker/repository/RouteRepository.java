package com.joseagim.traintracker.repository;

import com.joseagim.traintracker.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
