package com.example.demo.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class DisponibiliteDTO {

    private Long id;
    private Long medecinId;
    private String medecinNom;

    private DayOfWeek jourSemaine;
    private LocalDate dateDisponibilite;

    private LocalTime heureDebut;
    private LocalTime heureFin;
    private LocalTime heurePauseDebut;
    private LocalTime heurePauseFin;

    private Duration dureeCreneau;
    private boolean estActive;

    private List<LocalTime> creneaux; // Liste des créneaux générés
}
