package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Credential;
import Agence.AgenceUniversiteItalie_backEnd.entity.EnumUniversite;
import Agence.AgenceUniversiteItalie_backEnd.entity.UniversiteCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UniversiteCredentialRepository extends JpaRepository<UniversiteCredential, Long> {

    Optional<UniversiteCredential> findByUniveriste(EnumUniversite universite);
    List<UniversiteCredential> findByEmailUniversiteContaining(String email);
    List<UniversiteCredential> findByCredential(Credential credential);
}
