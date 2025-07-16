package com.example.demo.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpecialiteDTO {
    private Long id;

    @NotBlank(message = "Le nom de la spécialité est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @Size(max = 500)
    private String description;
}
