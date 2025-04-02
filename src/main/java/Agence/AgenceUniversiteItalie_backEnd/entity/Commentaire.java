package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCommentaire;

    @Column(length = 1000)
    private String contenu;

    private LocalDateTime dateCreationCommentaire ;

    @ManyToOne
    @JoinColumn(name = "tache_id", nullable = false)
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    public Commentaire(String contenu, Tache tache, Utilisateur utilisateur) {
        this.contenu = contenu;
        this.tache = tache;
        this.utilisateur = utilisateur;
    }

    @PrePersist
    protected void onCreate() {
        this.dateCreationCommentaire = LocalDateTime.now();
    }
}
