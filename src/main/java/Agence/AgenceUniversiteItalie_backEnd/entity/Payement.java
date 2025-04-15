package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Payement {

    // Ajout d'un class de tranche pour avoir la possibilite de plusieur tranche avec leur ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPayement;

    private BigDecimal montantaTotal;

    private LocalDate dateCreation;

    private String description;

    @Enumerated(EnumType.STRING)
    private StatusTranche statusTranche;

    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tranche> tranches = new ArrayList<>();



    @Enumerated(EnumType.STRING)
    private StatusPaiment statusPaiment=StatusPaiment.EN_COURS;



    @ManyToOne
    @JoinColumn(name = "Client_id", nullable = false)
    private Clients client;






}
