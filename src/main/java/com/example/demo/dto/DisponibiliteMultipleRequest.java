package com.example.demo.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class DisponibiliteMultipleRequest {
    private Long medecinId;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Duration dureeCreneau;
    private LocalTime heurePauseDebut;
    private LocalTime heurePauseFin;
    private List<LocalDate> dates;
}
