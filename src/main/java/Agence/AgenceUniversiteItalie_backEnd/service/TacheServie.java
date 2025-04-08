package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.EnumStatutTache;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tache;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.TacheRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TacheServie {

    @Autowired
    private  TacheRepository tacheRepository;

    @Autowired
    private  UtilisateurRepository utilisateurRepository;


    public TacheServie(TacheRepository tacheRepository, UtilisateurRepository utilisateurRepository) {
        this.tacheRepository = tacheRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     *


     * @param superAdminEmail
     * @param adminIds
     * @return Creation of Tache (first Solution)
     */

    public Tache createTacheTest(String titre,String description , String superAdminEmail, List<Long> adminIds) {

        Utilisateur superAdmin = utilisateurRepository.findByAdresseMail(superAdminEmail)
                .orElseThrow(() -> new RuntimeException("❌ SuperAdmin not found!"));

        if (superAdmin.getRole() == null || !superAdmin.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN)) {
            throw new RuntimeException("❌ Only Super Admins can create tasks!");
        }

        Tache tache = new Tache(titre,description, superAdmin);

        Set<Utilisateur> assignedAdmins = utilisateurRepository.findAllById(adminIds)
                .stream()
                .filter(u -> u.getRole() != null && u.getRole().getLibelleRole().equals(EnumRole.ADMIN)) // Prevent NullPointer
                .collect(Collectors.toSet());

        if (assignedAdmins.isEmpty()) {
            throw new RuntimeException("❌ At least one valid Admin is required!");
        }

        tache.setAssignedAdmins(assignedAdmins);

        return tacheRepository.save(tache);
    }


     public List<Tache> getAllTaches() {
        return tacheRepository.findAll();
     }

     public Optional<Tache> getTacheById(long id) {
        return tacheRepository.findById(id);
     }


    /**
     *
     * @param tache
     * @param superAdminEmail
     * @param adminIds
     * @return Creation of tache (Deuxiéme Solution)
     */
    public Tache createTache(Tache tache,String superAdminEmail, List<Long> adminIds){

        Utilisateur superAdmin = utilisateurRepository.findByAdresseMail(superAdminEmail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "superAdminNotFound"));

        if (!superAdmin.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, " Only Super Admin Can create a Task");
        }

        tache.setCreatedBy(superAdmin);

        // Assign the admin to the Task
        List<Utilisateur> adminList = utilisateurRepository.findAllById(adminIds);
        if (adminList.size() != adminIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some admins not found");
        }

        for (Utilisateur admin : adminList) {
            if (!admin.getRole().getLibelleRole().equals(EnumRole.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with ID is not an admin");
            }

        }

        tache.setAssignedAdmins(new HashSet<>(adminList));

        return tacheRepository.save(tache);
    }


    // get a Task By ID( we will not use )
    public Tache getTacheById(Long id){
        return tacheRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));
    }

    // get all tasks created by the superAdmin ( will be put in the dashboard of the superAdmin)
    public List<Tache> getTachesByCreator(String superAdminEmail){
        Utilisateur superAdmin = utilisateurRepository.findByAdresseMail(superAdminEmail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"SuperAdmin not found"));
        return tacheRepository.findByCreatedBy(superAdmin);
    }

    //get all tasks Assigned to an admin ( will be put in the admin dashboard)
    public List<Tache> getTachesAssignedToAdmin(String adminEmail){
        Utilisateur admin = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Admin not found"));

        if (admin.getRole().getLibelleRole() != EnumRole.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"User is not an admin");
        }

        return tacheRepository.findByAssignedAdminsContaining(admin);
    }


    //Update Task Status by Admin
    public Tache updateTacheStatus(Long tacheId, EnumStatutTache newStatus, String adminEmail){
        Utilisateur admin = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED," Admin not found"));

        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));

        if (!tache.getAssignedAdmins().contains(admin)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN," Admin not assigned to this Task");
        }

        tache.setStatus(newStatus);
        tache.setDateModification(LocalDateTime.now());

        return tacheRepository.save(tache);
    }

    public Tache addTasktToUser(Long idtask, Long iduser) {
		Utilisateur us = utilisateurRepository.findById(iduser).get();		
		Tache task = tacheRepository.findById(idtask).get();		
		task.setTakenBy(us);
		return tacheRepository.save(task);
    }
    
    public Utilisateur getUserTakenByIdtask(Long idtask) {
		Tache task = tacheRepository.findById(idtask).get();
		return task.getTakenBy();
	}
    
    //Delete a Task
    public void deleteTache(Long tacheId, String superAdminEmail){
        Utilisateur superAdmin = utilisateurRepository.findByAdresseMail(superAdminEmail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Super Admin not found"));

        if (superAdmin.getRole().getLibelleRole() != EnumRole.SUPER_ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only super admin can delete a Task");
        }

        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Tache not found"));

        tacheRepository.delete(tache);
    }


    public Tache updateTache(Tache task, long idtask) {
    	Tache task2 = tacheRepository.findById(idtask).get();
		task2.setTitre(task.getTitre());
		task2.setDescription(task.getDescription());
		task2.setPriority(task.getPriority());

		if(task.getStatus()==null) {
		task2.setStatus(EnumStatutTache.valueOf("PAS_ENCORE"));
		}else {
			task2.setStatus(task.getStatus());
		}
		return tacheRepository.save(task2);
	}


    public List<Utilisateur> FindUsersFromIdtask(Long idtask){
		return utilisateurRepository.findUsersByIdtask(idtask);
	}

    public long getTotalTasks() {
        return tacheRepository.countAllTasks();
    }

    public long getTasksEnCours() {
        return tacheRepository.countTasksEnCours();
    }

    public long getTasksDone() {
        return tacheRepository.countTasksDone();
    }
    
    public long countAllTasksAssignedByUser(Long userId) {
        return tacheRepository.countAllTasksAssignedByUser(userId);
    }

    public long countTasksEnCoursByUser(Long userId) {
        return tacheRepository.countTasksEnCoursByUser(userId);
    }

    public long countTasksDoneByUser(Long userId) {
        return tacheRepository.countTasksDoneByUser(userId);
    }

}