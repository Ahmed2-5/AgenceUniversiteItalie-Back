package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Payement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPayement;

    private int montantaPayer;
    private int resteAPayer=0;
    private int tranches;
}
