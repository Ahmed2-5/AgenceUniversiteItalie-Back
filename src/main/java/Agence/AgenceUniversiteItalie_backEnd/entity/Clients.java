package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
	    generator = ObjectIdGenerators.PropertyGenerator.class,
	    property = "idClients"
	)
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
    private EnumTypeService service;
    
    private String reference;

    private String programmedEtude;
    private String villeItalie;
    
    @Enumerated(EnumType.STRING)
    private EnumCommunication communication = EnumCommunication.NON;
    
    @Enumerated(EnumType.STRING)
    private Archive archive = Archive.NON_ARCHIVER;

    private String clientImageUrl;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Utilisateur clientCreatedby;

    @ManyToOne
    @JoinColumn(name = "assigned_to_tunisie")
    private Utilisateur assignedToTunisie;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payement> payementClient =new ArrayList<>();
 
    @OneToMany(mappedBy = "clientDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClientDocument> documents=new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "credential_id")
    private Credential credential;

    @ManyToOne
    @JoinColumn(name = "assigned_to_italie")
    private Utilisateur assignedToItalie;

}
