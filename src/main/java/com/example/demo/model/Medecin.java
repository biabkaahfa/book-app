package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@DiscriminatorValue("MEDECIN")
@Getter
@Setter
public class Medecin extends User {

    @ManyToOne(fetch = FetchType.EAGER) // EAGER pour éviter les proxy
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @Column(nullable = true)
    private String numeroLicence;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL)
    private List<Disponibilite> disponibilites;


   // @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL)
   @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous;

    public List<RendezVous> obtenirRendezVousAVenir() {
        return rendezVous.stream()
                .filter(rdv -> rdv.getDateRendezVous().isAfter(java.time.LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public void definirDisponibilite(Disponibilite disponibilite) {
        disponibilite.setMedecin(this);
        this.disponibilites.add(disponibilite);
    }
}
