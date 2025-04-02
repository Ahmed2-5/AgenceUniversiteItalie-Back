package Agence.AgenceUniversiteItalie_backEnd.entity;



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTache;

    private String titre;

    @Column(length = 1000)
    private String description;

    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private EnumPriority priority;

    @Enumerated(EnumType.STRING)
    private EnumStatutTache status;

    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime dateModification = LocalDateTime.now();

    private LocalDateTime dateEcheance = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private Utilisateur createdBy;

    @ManyToOne
    @JoinColumn(name = "TakenBy")
    private Utilisateur TakenBy;
    
    @ManyToMany
    @JoinTable(
            name = "tache_admins",
            joinColumns = @JoinColumn(name = "tache_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"tache_id", "admin_id"})
    )
    private Set<Utilisateur> assignedAdmins = new HashSet<>();

    @OneToMany(mappedBy = "tache" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Commentaire> commentaires = new HashSet<>();

    @PrePersist
    protected void onCreate(){
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        if (this.status == null){
            this.status= EnumStatutTache.PAS_ENCORE;
        }
        if (this.priority == null){
            this.priority=EnumPriority.Aucun;
        }
    }

    @PreUpdate
    protected void onUpdate(){
        this.dateModification = LocalDateTime.now();
    }


    public Tache(String titre, String description, Utilisateur createdBy) {
        this.titre = titre;
        this.description = description;
        this.createdBy = createdBy;
    }


    /*  Nahi hedhouma zeda ken mamchetech  */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tache tache = (Tache) o;
        return idTache !=null && idTache.equals(tache.idTache);
    }

    @Override
    public int hashCode(){
        return Objects.hash(idTache);
    }



}