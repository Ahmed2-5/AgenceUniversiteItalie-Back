package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Archive;
import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClientsRepository extends JpaRepository<Clients, Long> {

    List<Clients> findByArchive(Archive archive);

    List<Clients> findByCreatedBy_IdUtilisateur(Utilisateur idUtilisateur);

    List<Clients> findByCreatedBy_EmailAdmin(Utilisateur emailAdmin);

    Optional<Clients> findByEmailClient(String email);

    Optional<Clients> findClientsByPrenomClient(String prenomClient);

    @Query("SELECT client FROM Clients WHERE LOWER(client.nomClient) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(client.prenomClient) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ")
    List<Clients> searchClients(String searchTerm);
}
