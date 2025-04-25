package Agence.AgenceUniversiteItalie_backEnd.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RDV {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRDV;

	private String titreRDV;

    private LocalDateTime dateRendezVous;

    @Enumerated(EnumType.STRING)
    private EnumRendezVous enumRendezVous;
    
    @ManyToOne
    @JoinColumn(name = "credential_id")
    private Credential credential;
}
