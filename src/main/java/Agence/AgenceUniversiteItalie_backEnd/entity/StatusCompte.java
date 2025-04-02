package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class StatusCompte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatusCompte;
    private String libelleStatusCompte ;

}
