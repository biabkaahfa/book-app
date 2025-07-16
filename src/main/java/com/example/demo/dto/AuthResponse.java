package com.example.demo.dto;

// import lombok.Data; // supprimé car plus nécessaire

import com.example.demo.enums.Role;
import lombok.Data;


@Data
public class AuthResponse {
    private String token;
    private String nom;
    private String prenom;
    private String role;
private  long id;
    public AuthResponse( String nom, String prenom, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.role = role.name();
    }
    public AuthResponse( String token) {

        this.token = token;
    }



} 