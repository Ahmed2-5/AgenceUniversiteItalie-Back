package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Clients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClients;

    private String nomClient;
    private String prenomClient;

    private String telephoneClient;

    private String emailClient;

    private String adresseClient;
    private String villeClient;
    private int codePostale;

    private LocalDate dateNaissanceClient;

    private Langue langue;

    private Archive archive;



}
