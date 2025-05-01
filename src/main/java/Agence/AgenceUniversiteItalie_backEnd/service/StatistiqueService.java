package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.TrancheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatistiqueService {

    private final TrancheRepository trancheRepository;
    private final ClientsRepository clientsRepository;

    public Map<LocalDate, BigDecimal> montantRecuParJour() {
        return trancheRepository.getMontantRecuParJour().stream()
                .collect(Collectors.toMap(
                        r -> (LocalDate) r[0],
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
}
