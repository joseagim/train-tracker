package com.joseagim.traintracker.service;

import com.joseagim.traintracker.dto.request.StationRequestDto;
import com.joseagim.traintracker.dto.response.StationResponseDto;
import com.joseagim.traintracker.entity.Station;
import com.joseagim.traintracker.exception.ResourceNotFoundException;
import com.joseagim.traintracker.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponseDto create(StationRequestDto stationRequest) {

        Station station = new Station();
        station.setName(stationRequest.name());
        station.setCity(stationRequest.city());

        Station saved = stationRepository.save(station);

        return StationResponseDto.from(saved);
    }

    public StationResponseDto findById(Long id) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));

        return StationResponseDto.from(station);
    }

    public List<StationResponseDto> findAll() {

        return stationRepository.findAll().stream()
                .map(StationResponseDto::from)
                .collect(Collectors.toList());
    }

    public StationResponseDto update(Long id, StationRequestDto stationRequest) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));

        station.setName(stationRequest.name());
        station.setCity(stationRequest.city());

        Station saved = stationRepository.save(station);

        return StationResponseDto.from(saved);

    }

    public void delete(Long id) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));

        stationRepository.delete(station);

    }

}
