package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

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


    private boolean notificationEnvoyee = false;
    private boolean notificationRetardEnvoyee = false;



    @ManyToOne
    @JoinColumn(name = "paiement_id", nullable = false)
    @JsonBackReference
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
        this.payement.mettreAJourLeReste();
    }

}
