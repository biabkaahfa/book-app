package com.example.demo.model;

import com.example.demo.enums.StatutRendezVous;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "rendezvous")
@EntityListeners(AuditingEntityListener.class)
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    @JsonBackReference("patient-rdv")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medecin_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    @JsonBackReference("medecin-rdv")
    private Medecin medecin;


    @Column(nullable = false)
    @NotNull(message = "La date du rendez-vous est obligatoire")
    @Future(message = "La date du rendez-vous doit être dans le futur")
    private LocalDateTime dateRendezVous;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Le statut du rendez-vous est obligatoire")
    private StatutRendezVous statut = StatutRendezVous.PLANIFIE;

    @Column(nullable = false)
    @NotBlank(message = "Le motif est obligatoire")
    private String motif;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    private LocalDateTime dateMiseAJour;

    // Méthodes métier

    // Vérifie si un rendez-vous peut être annulé (plus de 24h à l'avance et statut correct)
    public boolean peutEtreAnnule() {
        return dateRendezVous.isAfter(LocalDateTime.now().plusHours(24)) &&
                (statut == StatutRendezVous.PLANIFIE || statut == StatutRendezVous.CONFIRME);
    }


}
