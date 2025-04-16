package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Tranche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTranche;

    private BigDecimal montant;
    private LocalDate dateLimite;
    private LocalDate dateResglement;
    private int numero;

    @Enumerated(EnumType.STRING)
    private StatusTranche statusTranche;



    @ManyToOne
    @JoinColumn(name = "paiement_id", nullable = false)
    private Payement payement;


    public Tranche(Payement payement, BigDecimal montant, LocalDate dateLimite, int numero) {
        this.payement = payement;
        this.montant = montant;
        this.dateLimite = dateLimite;
        this.numero = numero;
        this.statusTranche = StatusTranche.EN_ATTENTE;
    }

    public void marquerCommePayer(){
        this.dateResglement=LocalDate.now();
        this.statusTranche=StatusTranche.PAYEE;
        this.payement.verifierStatus();
    }

}
