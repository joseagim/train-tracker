package com.joseagim.traintracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Route {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopOrder")
    private List<RouteStation> routeStations = new ArrayList<>();

    @NotBlank
    @Column(nullable = false)
    private String name;

    public void addRouteStation(RouteStation rs) {
        rs.setRoute(this);
        routeStations.add(rs);
    }

    public int minutesBetween(Long originStationId, Long destinationStationId) {
        int originMinutes = -1;
        int destinationMinutes = -1;

        for (RouteStation rs : routeStations) {
            if (rs.getStation().getId().equals(originStationId)) {
                originMinutes = rs.getMinutesFromStart();
            }
            if (rs.getStation().getId().equals(destinationStationId)) {
                destinationMinutes = rs.getMinutesFromStart();
            }
        }

        return destinationMinutes - originMinutes;
    }

}
