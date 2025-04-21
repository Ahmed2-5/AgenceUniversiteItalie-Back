package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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



    @OneToOne(mappedBy = "credential", fetch = FetchType.LAZY)
    private Clients clients;

    @OneToMany(mappedBy = "credential", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UniversiteCredential> universiteCredentials = new ArrayList<>();



}
