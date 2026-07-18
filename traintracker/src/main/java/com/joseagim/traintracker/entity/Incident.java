package com.joseagim.traintracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Incident {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Station station;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Trip trip;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IncidentType type;

    @PositiveOrZero
    private int delayMinutes;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

}
