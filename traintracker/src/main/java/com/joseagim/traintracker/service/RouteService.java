package com.joseagim.traintracker.service;

import com.joseagim.traintracker.dto.request.RouteRequestDto;
import com.joseagim.traintracker.dto.request.RouteStationRequestDto;
import com.joseagim.traintracker.dto.response.RouteResponseDto;
import com.joseagim.traintracker.dto.response.RouteStationResponseDto;
import com.joseagim.traintracker.dto.response.StationResponseDto;
import com.joseagim.traintracker.entity.Route;
import com.joseagim.traintracker.entity.RouteStation;
import com.joseagim.traintracker.entity.Station;
import com.joseagim.traintracker.exception.ResourceNotFoundException;
import com.joseagim.traintracker.repository.RouteRepository;
import com.joseagim.traintracker.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;

    public RouteService(StationRepository stationRepository, RouteRepository routeRepository) {
        this.stationRepository = stationRepository;
        this.routeRepository = routeRepository;
    }

    public RouteResponseDto create(RouteRequestDto routeRequest) {

        Route route = new Route();
        route.setName(routeRequest.name());

        addRouteStations(routeRequest, route);

        Route saved = routeRepository.save(route);

        return new RouteResponseDto(saved.getId(), saved.getName(), getRouteStationsResponse(saved));

    }

    public RouteResponseDto findById(Long id) {

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        return new RouteResponseDto(route.getId(), route.getName(), getRouteStationsResponse(route));

    }

    public List<RouteResponseDto> findAll() {

        List<Route> routes = routeRepository.findAll();

        return routes.stream()
                .map(route -> new RouteResponseDto(route.getId(), route.getName(), getRouteStationsResponse(route)))
                .collect(Collectors.toList());

    }

    public RouteResponseDto update(Long id, RouteRequestDto routeRequest) {

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        route.setName(routeRequest.name());
        route.getRouteStations().clear();

        addRouteStations(routeRequest, route);

        Route saved = routeRepository.save(route);

        return new RouteResponseDto(saved.getId(), saved.getName(), getRouteStationsResponse(saved));

    }

    public void delete(Long id) {

        Route route = routeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        routeRepository.delete(route);

    }

    private void addRouteStations(RouteRequestDto routeRequest, Route route) {
        for (RouteStationRequestDto routeStationRequest : routeRequest.routeStations()) {
            RouteStation routeStation = new RouteStation();
            Station station = stationRepository.findById(routeStationRequest.stationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Station referenced with id: " + routeStationRequest.stationId() + " does not exist"));
            routeStation.setStation(station);
            routeStation.setStopOrder(routeStationRequest.stopOrder());
            routeStation.setMinutesFromStart(routeStationRequest.minutesFromStart());
            route.addRouteStation(routeStation);
        }
    }

    private static List<RouteStationResponseDto> getRouteStationsResponse(Route route) {
        List<RouteStationResponseDto> routeStationsResponse = new ArrayList<>();
        for (RouteStation savedRs : route.getRouteStations()) {
            routeStationsResponse.add(new RouteStationResponseDto(
                    savedRs.getId(),
                    new StationResponseDto(
                            savedRs.getStation().getId(),
                            savedRs.getStation().getName(),
                            savedRs.getStation().getCity()),
                    savedRs.getStopOrder(),
                    savedRs.getMinutesFromStart()
            ));
        }
        return routeStationsResponse;
    }

}
