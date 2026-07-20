package com.joseagim.traintracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Ticket {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String uuid;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Trip trip;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Station origin;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Station destination;

    @Positive
    private int bogey;

    @Positive
    private int seat;

    @Positive
    private double price;

    @PrePersist
    public void prePersist() {
        uuid = UUID.randomUUID().toString();
    }

}
