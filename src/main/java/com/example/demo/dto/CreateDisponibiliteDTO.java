package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateDisponibiliteDTO {
    @NotNull(message = "Le médecin est obligatoire")
    private Long medecinId;

    @NotNull(message = "Le jour de la semaine est obligatoire")
    private DayOfWeek jourSemaine;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotNull(message = "La durée d'un créneau est obligatoire")
    @Positive(message = "La durée d'un créneau doit être positive")
    private Duration dureeCreneau;
    @NotNull(message = "L'heure de début de pause est obligatoire")
    private LocalTime heurePauseDebut;

    @NotNull(message = "L'heure de fin de pause est obligatoire")
    private LocalTime heurePauseFin;
    @NotNull(message = "La date de disponibilité est obligatoire")
    private LocalDate dateDisponibilite;

}
