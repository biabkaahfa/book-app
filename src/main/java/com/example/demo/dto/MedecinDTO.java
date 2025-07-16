package com.example.demo.dto;

import lombok.Data;

@Data
public class MedecinDTO {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private String numeroLicence;
    private String description;
    private SpecialiteDTO specialite;

}
