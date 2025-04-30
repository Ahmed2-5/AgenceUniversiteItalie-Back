package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.LogAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface LogActionRepository extends JpaRepository<LogAction, Long> {


    List<LogAction> findByTypeEntiteAndIdEntite(String typeEntite, Long idEntite);

    List<LogAction> findByAdmin_IdUtilisateur(Long idUtilisateur);

    List<LogAction> findByDateActionBetween(LocalDateTime debut, LocalDateTime fin);

    List<LogAction> findByTitreContaining(String titre);
    
}
