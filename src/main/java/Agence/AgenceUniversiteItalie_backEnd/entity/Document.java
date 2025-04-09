package Agence.AgenceUniversiteItalie_backEnd.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    private String nom;

    private String cheminFichier ;

    private LocalDateTime dateAjout;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Clients clientDocument;


    @ManyToOne
    @JoinColumn(name = "ajouter_par_id", nullable = false)
    private Utilisateur ajouterPar;


    @PrePersist
    protected void onCreate() {
        this.dateAjout = LocalDateTime.now();
    }

    public Document(String nom, String cheminFichier, Clients client, Utilisateur ajouterPar) {
        this.nom = nom;
        this.cheminFichier = cheminFichier;
        this.clientDocument = client;
        this.ajouterPar=ajouterPar;
    }


}
