package com.example.demo.dto;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DisponibiliteHebdoRequest {
    private Long medecinId;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Duration dureeCreneau;
    private LocalTime heurePauseDebut;
    private LocalTime heurePauseFin;
    private LocalDate startDate;
}
