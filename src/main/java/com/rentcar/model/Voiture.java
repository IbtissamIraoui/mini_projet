package com.rentcar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voiture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "L'immatriculation est obligatoire")
    @Column(unique = true)
    private String immatriculation;
    
    @NotBlank(message = "La marque est obligatoire")
    private String marque;
    
    private String segment;
    
    private boolean disponible = true;
    
    @Min(value = 0, message = "Le prix par jour doit être positif")
    private double prixJour;
}
