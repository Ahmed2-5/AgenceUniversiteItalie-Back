package Agence.AgenceUniversiteItalie_backEnd.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtilisateur;

    private String nom;
    private String prenom;

    @JsonProperty("adresseMail")
    private String adresseMail;

    @JsonProperty("motDePasse")
    private String motDePasse;

    private String telephone;

    private LocalDate dateDeNaissance;

    private Long idTypeAuthentification;

    private String idFacebook;

    private String idGoogle;

    private LocalDateTime dateCreation;

    private LocalDateTime dateDerniereConnexion;

    private String profileImageUrl;


    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "id_status-compte")
    private StatusCompte statusCompte;


    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Tache> createdTaches = new HashSet<>();

    @OneToMany(mappedBy = "TakenBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Tache> tasksTaked = new HashSet<>();
    
    @ManyToMany(mappedBy = "assignedAdmins")
    @JsonIgnore
    private Set<Tache> assignedTaches = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Commentaire> commentaires = new HashSet<>();

    @OneToMany(mappedBy = "ajouterPar", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Document> documentAdded = new HashSet<>();

    @OneToMany(mappedBy = "clientCreatedby",cascade = {CascadeType.MERGE , CascadeType.PERSIST})
    @JsonIgnore
    private Set<Clients> clientsCreated=new HashSet<>();

    // ajout d'un constructeur
    public Utilisateur(String nom, String prenom, String adresseMail, String motDePasse, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresseMail = adresseMail;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateCreation = LocalDateTime.now();
        this.dateDerniereConnexion =LocalDateTime.now();
    }

    public String getMotDePasse() {return motDePasse;}

    @PrePersist
    protected void onCreate() {this.dateCreation = LocalDateTime.now();} //Définition automatique de la date


    /*       Nahi les deux Fonctions edhouma ken mamchetech              */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return idUtilisateur != null && idUtilisateur.equals(that.idUtilisateur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUtilisateur);
    }

}
