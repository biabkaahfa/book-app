package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "disponibilite")
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medecin_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    private Medecin medecin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Le jour de la semaine est obligatoire")
    private DayOfWeek jourSemaine;

    @Column(nullable = false)
    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @Column(nullable = false)
    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @Column(nullable = false)
    @NotNull(message = "La durée d'un créneau est obligatoire")
    private Duration dureeCreneau;

    @Column(nullable = false)
    @NotNull(message = "Le debut de pause est obligatoire")
    private LocalTime heurePauseDebut;
    @Column(nullable = false)
    @NotNull(message = "La fin de pause est obligatoire")
    private LocalTime heurePauseFin;


    @Column(nullable = false)
    private boolean estActive = true;
    @Column(name = "date_disponibilite", nullable = false)
    @NotNull(message = "La date de la disponibilité est obligatoire")
    private LocalDate dateDisponibilite;

    // Méthodes métier
    public List<LocalTime> obtenirCreneauxDisponibles() {
        List<LocalTime> creneaux = new ArrayList<>();
        LocalTime current = heureDebut;

        while (current.plus(dureeCreneau).isBefore(heureFin.plusSeconds(1))) {
            // Filtrer si dans la pause
            if (heurePauseDebut != null && heurePauseFin != null) {
                if (!current.isBefore(heurePauseDebut) && current.isBefore(heurePauseFin)) {
                    current = heurePauseFin;
                    continue;
                }
            }

            creneaux.add(current);
            current = current.plus(dureeCreneau);
        }

        return creneaux;
    }

}
