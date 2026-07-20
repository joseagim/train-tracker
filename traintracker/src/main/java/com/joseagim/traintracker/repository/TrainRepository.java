package com.joseagim.traintracker.repository;

import com.joseagim.traintracker.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepository extends JpaRepository<Train, Long> {
}
