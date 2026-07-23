package com.joseagim.traintracker.service;

import com.joseagim.traintracker.dto.request.TrainRequestDto;
import com.joseagim.traintracker.dto.response.TrainResponseDto;
import com.joseagim.traintracker.entity.Train;
import com.joseagim.traintracker.exception.ResourceNotFoundException;
import com.joseagim.traintracker.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainService {

    private final TrainRepository trainRepository;

    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    public TrainResponseDto create(TrainRequestDto trainRequest) {

        Train train = new Train();
        train.setType(trainRequest.type());
        train.setBogeys(trainRequest.bogeys());
        train.setSeatsByBogey(trainRequest.seatsByBogey());

        Train saved = trainRepository.save(train);

        return new TrainResponseDto(saved.getId(), saved.getType(), saved.getBogeys(), saved.getSeatsByBogey());

    }

    public TrainResponseDto findById(Long id) {

        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));

        return new TrainResponseDto(train.getId(), train.getType(), train.getBogeys(), train.getSeatsByBogey());

    }

    public List<TrainResponseDto> findAll() {

        return trainRepository.findAll().stream()
                .map(train ->
                        new TrainResponseDto(train.getId(), train.getType(), train.getBogeys(), train.getSeatsByBogey()))
                .collect(Collectors.toList());

    }

    public TrainResponseDto edit(Long id, TrainRequestDto trainRequest) {

        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));

        train.setType(trainRequest.type());
        train.setBogeys(trainRequest.bogeys());
        train.setSeatsByBogey(trainRequest.seatsByBogey());

        Train saved = trainRepository.save(train);

        return new TrainResponseDto(saved.getId(), saved.getType(), saved.getBogeys(), saved.getSeatsByBogey());

    }

    public void delete(Long id) {

        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));

        trainRepository.delete(train);

    }

}
