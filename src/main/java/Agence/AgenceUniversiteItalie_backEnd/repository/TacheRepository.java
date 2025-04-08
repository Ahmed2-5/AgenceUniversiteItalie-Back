package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Tache;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TacheRepository extends JpaRepository<Tache,Long>   {
    
    List<Tache> findByAssignedAdminsContaining(Utilisateur admin);

    List<Tache> findByCreatedBy(Utilisateur createdBy);

    @Query("SELECT COUNT(t) FROM Tache t")
    long countAllTasks();

    @Query("SELECT COUNT(t) FROM Tache t WHERE t.status = 'EN_COURS'")
    long countTasksEnCours();

    @Query("SELECT COUNT(t) FROM Tache t WHERE t.status = 'DONE'")
    long countTasksDone();

    @Query("SELECT COUNT(t) FROM Tache t JOIN t.assignedAdmins a WHERE a.idUtilisateur = :userId")
    long countAllTasksAssignedByUser(Long userId);

    @Query("SELECT COUNT(t) FROM Tache t JOIN t.assignedAdmins a WHERE a.idUtilisateur = :userId AND t.status = 'EN_COURS'")
    long countTasksEnCoursByUser(Long userId);

    @Query("SELECT COUNT(t) FROM Tache t JOIN t.assignedAdmins a WHERE a.idUtilisateur = :userId AND t.status = 'DONE'")
    long countTasksDoneByUser(Long userId);
}
