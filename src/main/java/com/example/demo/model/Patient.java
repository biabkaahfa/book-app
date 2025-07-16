package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@DiscriminatorValue("PATIENT")
@Getter
@Setter
public class Patient extends User {

    @Column(nullable = true)
    private String adresse;

    @Column(name = "date_naissance", nullable = true)
    private LocalDate dateNaissance;

    @Column(name = "contact_urgence", nullable = true)
    private String contactUrgence;


    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous;

    public List<RendezVous> obtenirHistoriqueMedical() {
        return rendezVous;
    }
}

