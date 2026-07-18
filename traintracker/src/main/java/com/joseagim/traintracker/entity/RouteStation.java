package com.joseagim.traintracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class RouteStation {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Route route;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Station station;

    @Positive
    private int stopOrder;

    @PositiveOrZero
    private int minutesFromStart;

}
