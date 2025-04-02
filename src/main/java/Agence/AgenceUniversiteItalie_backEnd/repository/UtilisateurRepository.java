package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Role;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {
    Optional<Utilisateur> findByRole(Role role);
    Optional<Utilisateur> findByRole_LibelleRole(EnumRole libelleRole);
    Optional<Utilisateur> findByAdresseMail(String adresseMail);
    Optional<List<Utilisateur>> findAllByRole_LibelleRole(EnumRole libelleRole);
    
    @Query("SELECT u FROM Utilisateur u JOIN u.assignedTaches t WHERE t.idTache = ?1")
    public List<Utilisateur> findUsersByIdtask(Long idtask);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role.libelleRole = 'ADMIN'")
    long countAdmins(); 
    
    @Query("SELECT COUNT(u) FROM Utilisateur u")
    long countAllUsers();

}
