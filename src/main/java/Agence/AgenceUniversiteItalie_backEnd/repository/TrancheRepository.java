package Agence.AgenceUniversiteItalie_backEnd.repository;


import Agence.AgenceUniversiteItalie_backEnd.entity.Payement;
import Agence.AgenceUniversiteItalie_backEnd.entity.StatusTranche;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tranche;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrancheRepository extends JpaRepository<Tranche, Long> {
    List<Tranche> findByStatusTrancheAndDateLimiteLessThan(StatusTranche statusTranche, LocalDate limite);
    List<Tranche> findByStatusTrancheAndDateLimite(StatusTranche statusTranche, LocalDate limite);
    List<Tranche> findByPayement(Payement payment);



}
