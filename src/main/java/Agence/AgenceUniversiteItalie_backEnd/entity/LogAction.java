package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class LogAction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLog;

    private String titre;
    private String contenu;

    private String typeEntite;

    private Long idEntite;

    private LocalDateTime dateAction;

    @ManyToOne
    @JoinColumn(name = "id_admin")
    private Utilisateur admin;


    @PrePersist
    protected void onCreate() {
        this.dateAction = LocalDateTime.now();
    }

    public LogAction(String titre, String contenu, String typeEntite,Long idEntite, Utilisateur admin) {
        this.titre = titre;
        this.contenu = contenu;
        this.typeEntite = typeEntite;
        this.idEntite= idEntite;
        this.admin = admin;
        this.dateAction = LocalDateTime.now();
    }


}
