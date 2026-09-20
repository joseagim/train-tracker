package com.joseagim.traintracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Trip {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Route route;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Train train;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime departureTime;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.ON_TIME;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String seats;

    @Version
    private Long version;

    public double getPrice(Long originId, Long destinationId) {
        double baseFare = route.minutesBetween(originId, destinationId) * 0.15;
        double timeFactor = getTimeFactor(departureTime.toLocalTime());
        double dayFactor = getDayFactor(departureTime.getDayOfWeek());
        return baseFare * timeFactor * dayFactor;
    }

    private double getTimeFactor(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 6 && hour < 9) return 0.85;
        if (hour >= 9 && hour < 21) return 1.15;
        return 1.0;
    }

    private double getDayFactor(DayOfWeek day) {
        return switch (day) {
            case SATURDAY, SUNDAY -> 1.2;
            case FRIDAY -> 1.1;
            default -> 1.0;
        };
    }

}
