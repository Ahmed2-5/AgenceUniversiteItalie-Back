package Agence.AgenceUniversiteItalie_backEnd.repository;

import java.time.LocalDateTime;
import java.util.List;

import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Credential;
import Agence.AgenceUniversiteItalie_backEnd.entity.RDV;


@Repository
public interface RDVRepository extends JpaRepository<RDV, Long> {

    List<RDV> findByCredential(Credential credential);
    List<RDV> findRDVByEnumRendezVousAndDateRendezVous(EnumRendezVous enumRendezVous, LocalDateTime dateRdv);
}
