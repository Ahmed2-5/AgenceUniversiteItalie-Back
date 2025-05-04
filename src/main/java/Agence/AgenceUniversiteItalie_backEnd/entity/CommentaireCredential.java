package Agence.AgenceUniversiteItalie_backEnd.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class CommentaireCredential {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCommentaireCredential;

    @Column(length = 1000)
    private String contenuCommentaireCredential;

    private LocalDateTime dateCreationCommentaireCredential ;

    @ManyToOne
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    public CommentaireCredential(String contenu, Credential credential, Utilisateur utilisateur) {
        this.contenuCommentaireCredential = contenu;
        this.credential = credential;
        this.utilisateur = utilisateur;
    }

    @PrePersist
    protected void onCreate() {
        this.dateCreationCommentaireCredential = LocalDateTime.now();
    }
}
