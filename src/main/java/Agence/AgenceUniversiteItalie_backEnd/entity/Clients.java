package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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


    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Utilisateur clientCreatedby;


    @OneToMany(mappedBy = "clients", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payement> payementClient =new ArrayList<>();

    @OneToMany(mappedBy = "clientDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents=new ArrayList<>();





}
