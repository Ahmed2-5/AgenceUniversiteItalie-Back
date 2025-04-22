package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    private LocalDateTime dateRendezVous;

    @Enumerated(EnumType.STRING)
    private EnumRendezVous enumRendezVous;

    private String programmeEtude;

    @Enumerated(EnumType.STRING)
    private PreInscrit preInscrit; // if done envoyer une notification pour upload le dossier

    private LocalDateTime dateTestItalien;


    // sauf pour les admin italie.
    private int montantPayerItalie;


    @JsonIgnore
    @OneToOne(mappedBy = "credential")
    private Clients clients;

    @OneToMany(mappedBy = "credential", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UniversiteCredential> universiteCredentials = new ArrayList<>();



}
