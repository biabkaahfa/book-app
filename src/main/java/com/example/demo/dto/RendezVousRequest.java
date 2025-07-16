package com.example.demo.dto;

import com.example.demo.enums.StatutRendezVous;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class RendezVousRequest {
    private Long id;
    private Long patientId;
    private String patientNom;
    private Long medecinId;
    private String medecinNom;
    private LocalDateTime dateRendezVous;
    private StatutRendezVous statut;
    private String motif;
    private String notes;
}
