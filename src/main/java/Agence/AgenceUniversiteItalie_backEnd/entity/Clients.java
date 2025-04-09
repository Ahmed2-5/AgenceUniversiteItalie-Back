package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private Langue langue;

    @Enumerated(EnumType.STRING)
    private Archive archive;


    @OneToOne(mappedBy = "clients", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payement payementClient;



}
