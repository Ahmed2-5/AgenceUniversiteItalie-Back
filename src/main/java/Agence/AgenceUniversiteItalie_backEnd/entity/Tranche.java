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



    @ManyToOne
    @JoinColumn(name = "paiement_id", nullable = false)
    private Payement payement;


}
