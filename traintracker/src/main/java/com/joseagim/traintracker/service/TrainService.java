package com.joseagim.traintracker.service;

import com.joseagim.traintracker.dto.request.TrainRequestDto;
import com.joseagim.traintracker.dto.response.TrainResponseDto;
import com.joseagim.traintracker.entity.Train;
import com.joseagim.traintracker.exception.ResourceNotFoundException;
import com.joseagim.traintracker.repository.TrainRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        return TrainResponseDto.from(saved);

    }

    public TrainResponseDto findById(Long id) {

        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));

        return TrainResponseDto.from(train);

    }

    public Page<TrainResponseDto> findAll(Pageable pageable) {
        return trainRepository.findAll(pageable).map(TrainResponseDto::from);
    }

    public TrainResponseDto update(Long id, TrainRequestDto trainRequest) {

        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));

        train.setType(trainRequest.type());
        train.setBogeys(trainRequest.bogeys());
        train.setSeatsByBogey(trainRequest.seatsByBogey());

        Train saved = trainRepository.save(train);

        return TrainResponseDto.from(saved);

    }

    public void delete(Long id) {

        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));

        trainRepository.delete(train);

    }

}
