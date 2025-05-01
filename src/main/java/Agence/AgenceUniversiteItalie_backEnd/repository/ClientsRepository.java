package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Archive;
import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ClientsRepository extends JpaRepository<Clients, Long> {

    List<Clients> findByArchive(Archive archive);

    List<Clients> findClientsByClientCreatedby(Utilisateur adressMail);

    List<Clients> findClientsByAssignedToTunisie(Utilisateur adresseMail);

    List<Clients> findClientsByAssignedToItalie(Utilisateur adresseMail);

    //List<Clients> findByCreatedBy_EmailAdmin(Utilisateur emailAdmin);

    //Optional<Clients> findByEmailClient(String email);

    //Optional<Clients> findClientsByPrenomClient(String prenomClient);


    @Query("SELECT client FROM Clients client WHERE LOWER(client.nomClient) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(client.prenomClient) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ")
    List<Clients> searchClients(String searchTerm);

    @Query("SELECT CONCAT(u.prenom, ' ', u.nom), COUNT(c) " +
            "FROM Clients c " +
            "JOIN c.assignedToTunisie u " +
            "GROUP BY CONCAT(u.prenom, ' ', u.nom)")
    List<Object[]> getNombreClientsParAdmin();
}
