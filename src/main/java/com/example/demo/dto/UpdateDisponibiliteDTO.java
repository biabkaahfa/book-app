package com.example.demo.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

@Data
public class UpdateDisponibiliteDTO {
    private DayOfWeek jourSemaine;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    @Positive(message = "La durée d'un créneau doit être positive")
    private Duration dureeCreneau;

    private Boolean estActive;
}