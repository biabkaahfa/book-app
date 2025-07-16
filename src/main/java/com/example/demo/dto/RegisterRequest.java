package com.example.demo.dto;

import com.example.demo.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String prenom;
    private String nom;
    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Numéro de téléphone invalide")
    private String telephone;
    private Role role;

    // Si Patient
    private String adresse;
    private LocalDate dateNaissance;
    private String contactUrgence;

    // Si Médecin (optionnel si gestion multi-rôle)
    private String numeroLicence;
    private Long specialiteId;
    private String description;
} 