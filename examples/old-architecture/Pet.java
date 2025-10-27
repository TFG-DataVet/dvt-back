// ENFOQUE ANTIGUO - Dependencias directas entre entidades
package com.datavet.datavet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String species;
    private String breed;
    
    // ❌ PROBLEMA: Dependencia directa a otras entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;  // Dependencia directa
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;  // Dependencia directa
    
    // Esto crea acoplamiento fuerte entre dominios
}