package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.TrancheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatistiqueService {

    private final TrancheRepository trancheRepository;
    private final ClientsRepository clientsRepository;

    public Map<LocalDate, BigDecimal> montantRecuParJour() {
        return trancheRepository.getMontantRecuParJour().stream()
                .collect(Collectors.toMap(
                        r -> ((java.sql.Date) r[0]).toLocalDate(), // ✅ conversion explicite
                        r -> (BigDecimal) r[1]
                ));
    }


    public Map<Integer, BigDecimal> montantRecuParSemaine() {
        return trancheRepository.getMontantRecuParSemaine().stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> (BigDecimal) r[1]
                ));
    }

    public Map<Integer, BigDecimal> montantRecuParMois() {
        return trancheRepository.getMontantRecuParMois().stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> (BigDecimal) r[1]
                ));
    }

    public Map<String, Long> nombreClientParAdmin() {
        return clientsRepository.getNombreClientsParAdmin().stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> ((Number) r[1]).longValue()
                ));
    }

    public Map<String, BigDecimal> montantTotalRecuParAdmin() {
        return trancheRepository.getMontantTotalRecuParAdmin().stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (BigDecimal) r[1]
                ));
    }

    public Map<LocalDate, BigDecimal> montantAttenduParJour() {
        return trancheRepository.getMontantAttenduParJour().stream()
                .collect(Collectors.toMap(
                        r -> ((java.sql.Date) r[0]).toLocalDate(), // ✅ conversion explicite
                        r -> (BigDecimal) r[1]
                ));
    }


    public Map<Integer, BigDecimal> montantAttenduParSemaine() {
        return trancheRepository.getMontantAttenduParSemaine().stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> (BigDecimal) r[1]
                ));
    }

    public Map<Integer, BigDecimal> montantAttenduParMois() {
        return trancheRepository.getMontantAttenduParMois().stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> (BigDecimal) r[1]
                ));
    }

    public Map<Integer, Map<String, BigDecimal>> comparaisonParMois() {
        Map<Integer, BigDecimal> recu = montantRecuParMois();
        Map<Integer, BigDecimal> attendu = montantAttenduParMois();

        Set<Integer> tousLesMois = new HashSet<>();
        tousLesMois.addAll(recu.keySet());
        tousLesMois.addAll(attendu.keySet());

        Map<Integer, Map<String, BigDecimal>> resultat = new HashMap<>();

        for (Integer mois : tousLesMois) {
            BigDecimal montantRecu = recu.getOrDefault(mois, BigDecimal.ZERO);
            BigDecimal montantAttendu = attendu.getOrDefault(mois, BigDecimal.ZERO);

            Map<String, BigDecimal> comparaison = new HashMap<>();
            comparaison.put("recu", montantRecu);
            comparaison.put("attendu", montantAttendu);

            resultat.put(mois, comparaison);
        }

        return resultat;
    }


}
