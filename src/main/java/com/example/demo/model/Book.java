package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre ne peut pas être vide")
    @Size(min = 1, max = 255, message = "Le titre doit contenir entre 1 et 255 caractères")
    @Column(nullable = false)
    private String titre;

    @NotBlank(message = "L'auteur ne peut pas être vide")
    @Size(min = 1, max = 255, message = "Le nom de l'auteur doit contenir entre 1 et 255 caractères")
    @Column(nullable = false)
    private String auteur;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "La date de publication est requise")
    @PastOrPresent(message = "La date de publication ne peut pas être dans le futur")
    @Column(nullable = false)
    private LocalDate datePublication;

    // Constructeurs
    public Book() {
    }

    public Book(String titre, String auteur, String description, LocalDate datePublication) {
        this.titre = titre;
        this.auteur = auteur;
        this.description = description;
        this.datePublication = datePublication;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", datePublication=" + datePublication +
                '}';
    }
}

