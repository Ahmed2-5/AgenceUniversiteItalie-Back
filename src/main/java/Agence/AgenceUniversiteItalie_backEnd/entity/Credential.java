package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Credential {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCredential;

    private String emailOutlook;
    private String passwrodOutlook;

    private String emailGmail;
    private String passwrodGmail;

    private String prenotami;
    private String passwordPrenotami;

    private String universitaly;
    private String passwordUniversitaly;
    
    private String programmeEtude;

    @Enumerated(EnumType.STRING)
    private PreInscrit preInscrit= PreInscrit.PAS_ENCORE; // if done envoyer une notification pour upload le dossier

    // sauf pour les admin italie.
  //  private int montantPayerItalie;
    

    @JsonIgnore
    @OneToOne(mappedBy = "credential")
    private Clients clients;

    @OneToMany(mappedBy = "credential", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RDV> RDVs = new ArrayList<>();
    
    @OneToMany(mappedBy = "credential", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UniversiteCredential> universiteCredentials = new ArrayList<>();

    @OneToMany(mappedBy = "credential" , cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<CommentaireCredential> commentaires = new HashSet<>();

}
