package Agence.AgenceUniversiteItalie_backEnd.repository;



import Agence.AgenceUniversiteItalie_backEnd.entity.StatusTranche;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tranche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TrancheRepository extends JpaRepository<Tranche, Long> {
    List<Tranche> findByStatusTrancheAndDateLimiteLessThanEqual(StatusTranche statusTranche, LocalDate limite);
    List<Tranche> findByStatusTrancheAndDateLimite(StatusTranche statusTranche, LocalDate limite);
    List<Tranche> findByPayementIdPayement(Long paymentId);

    @Query("SELECT DATE(t.dateResglement), SUM(t.montant) " +
            "FROM Tranche t " +
            "WHERE t.statusTranche = 'PAYEE' AND t.dateResglement IS NOT NULL " +
            "GROUP BY DATE(t.dateResglement) " +
            "ORDER BY DATE(t.dateResglement)")
    List<Object[]> getMontantRecuParJour();


    @Query("SELECT FUNCTION('WEEK', t.dateResglement), SUM(t.montant) " +
            "FROM Tranche t " +
            "WHERE t.statusTranche = 'PAYEE' AND t.dateResglement IS NOT NULL " +
            "GROUP BY FUNCTION('WEEK', t.dateResglement)")
    List<Object[]> getMontantRecuParSemaine();

    @Query("SELECT MONTH(t.dateResglement), SUM(t.montant) " +
            "FROM Tranche t " +
            "WHERE t.statusTranche = 'PAYEE' AND t.dateResglement IS NOT NULL " +
            "GROUP BY MONTH(t.dateResglement)")
    List<Object[]> getMontantRecuParMois();

    @Query("SELECT CONCAT(u.prenom, ' ', u.nom), SUM(t.montant) " +
            "FROM Tranche t " +
            "JOIN t.payement p " +
            "JOIN p.client c " +
            "JOIN c.clientCreatedby u " +
            "WHERE t.statusTranche = 'PAYEE' " +
            "GROUP BY CONCAT(u.prenom, ' ', u.nom)")
    List<Object[]> getMontantTotalRecuParAdmin();
}




