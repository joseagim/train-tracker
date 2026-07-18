package com.joseagim.traintracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "trip")
    private List<Incident> incidents = new ArrayList<>();

    @NotNull
    @Column(nullable = false)
    private LocalDateTime departureTime;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.ON_TIME;

    @NotBlank
    @Column(nullable = false)
    private String seats;

    @Version
    private Long version;

    public void addIncident(Incident i) {
        i.setTrip(this);
        incidents.add(i);
    }

}
