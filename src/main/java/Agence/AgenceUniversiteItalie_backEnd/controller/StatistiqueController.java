package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    @GetMapping("/recu/jour")
    public Map<LocalDate, BigDecimal> montantParJour() {
        return statistiqueService.montantRecuParJour();
    }

    @GetMapping("/recu/semaine")
    public Map<Integer, BigDecimal> montantParSemaine() {
        return statistiqueService.montantRecuParSemaine();
    }

    @GetMapping("/recu/mois")
    public Map<Integer, BigDecimal> montantParMois() {
        return statistiqueService.montantRecuParMois();
    }

    @GetMapping("/clients/admins")
    public Map<String, Long> clientsParAdmin() {
        return statistiqueService.nombreClientParAdmin();
    }

    @GetMapping("/recu/admins")
    public Map<String, BigDecimal> montantRecuParAdmin() {
        return statistiqueService.montantTotalRecuParAdmin();
    }
}
