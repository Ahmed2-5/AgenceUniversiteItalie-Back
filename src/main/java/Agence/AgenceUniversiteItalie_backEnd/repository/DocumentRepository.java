package Agence.AgenceUniversiteItalie_backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Agence.AgenceUniversiteItalie_backEnd.entity.ClientDocument;

import java.util.List;

public interface DocumentRepository extends JpaRepository<ClientDocument,Long> {

    List<ClientDocument> findByClientDocument_IdClients(Long idClient);

    List<ClientDocument> findDocumentByAjouterPar_IdUtilisateur(Long idUtilisateur);

    List<ClientDocument> findDocumentByNomContainingIgnoreCase(String nom);
}
