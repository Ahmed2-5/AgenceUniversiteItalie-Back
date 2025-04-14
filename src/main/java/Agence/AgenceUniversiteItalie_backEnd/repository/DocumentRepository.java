package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document,Long> {

    List<Document> findByClientDocument_IdClients(Long idClient);

    List<Document> findDocumentByAjouterPar_IdUtilisateur(Long idUtilisateur);

    List<Document> findDocumentByNomContainingIgnoreCase(String nom);
}
