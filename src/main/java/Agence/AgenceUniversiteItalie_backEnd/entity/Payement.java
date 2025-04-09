package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
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
    private int resteaPayer=0;
    private int tranches;


    @Enumerated(EnumType.STRING)
    private StatusPaiment statusPaiment=StatusPaiment.EN_ATTENTE;



    @ManyToOne
    @JoinColumn(name = "Client_id", nullable = false)
    private Clients clients;



}
