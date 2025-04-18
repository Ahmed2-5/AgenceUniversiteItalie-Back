package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Payement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPayement;

    private BigDecimal montantaTotal;

    private LocalDate dateCreation;

    private BigDecimal leReste;

    @Enumerated(EnumType.STRING)
    private StatusPaiment statusPaiment;


    @OneToMany(mappedBy = "payement", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Tranche> tranches = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "Client_id", nullable = false)
    @JsonBackReference
    private Clients client;


    public Payement(Clients client, BigDecimal montantaTotal) {
        this.client = client;
        this.montantaTotal = montantaTotal;
        this.dateCreation = LocalDate.now();
        this.statusPaiment= StatusPaiment.EN_COURS;
    }
    
    public void mettreAJourLeReste() {
        this.leReste = tranches.stream()
                .filter(t -> t.getStatusTranche() == StatusTranche.EN_ATTENTE || t.getStatusTranche() == StatusTranche.EN_RETARD)
                .map(Tranche::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void diviserEnTranche(int nombreTranches){
        if (nombreTranches<1 || nombreTranches>5){
            throw new IllegalArgumentException("Nombre de tranches invalid");
        }

        this.tranches.clear();

        BigDecimal montantParTranche = this.montantaTotal.divide(BigDecimal.valueOf(nombreTranches), 2, BigDecimal.ROUND_HALF_UP);


        LocalDate dateLimite = LocalDate.now();
        for (int i=0; i<nombreTranches; i++){

            dateLimite=dateLimite.plusMonths(1);
            Tranche tranche = new Tranche(this,montantParTranche,dateLimite,i+1);
            this.tranches.add(tranche);
        }
        this.mettreAJourLeReste();
    }


    public void verifierStatus(){
        boolean toutespayer = tranches.stream().allMatch(t -> t.getStatusTranche() == StatusTranche.PAYEE);
        if (toutespayer){
            this.statusPaiment=StatusPaiment.COMPLETE;
        }
    }





}
