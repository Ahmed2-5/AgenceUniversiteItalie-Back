package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.Commentaire;
import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Notification;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tache;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.CommentaireRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.NotificationRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.TacheRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentaireService {
    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationRepository notifrep;
    
    // Add a comment to a task
    public Commentaire addCommentToTache(Long tacheId, String contenu, String userEmail) {
        Utilisateur utilisateur = utilisateurRepository.findByAdresseMail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        // Check if the user is allowed to comment (admin assigned to the task or super admin creator)
        boolean isAssignedAdmin = utilisateur.getRole().getLibelleRole() == EnumRole.ADMIN_TUNISIE &&
                tache.getAssignedAdmins().contains(utilisateur);
        boolean isAssignedAdminItalie = utilisateur.getRole().getLibelleRole() == EnumRole.ADMIN_ITALIE &&
                tache.getAssignedAdmins().contains(utilisateur);
        boolean isSuperAdmin = utilisateur.getRole().getLibelleRole() == EnumRole.SUPER_ADMIN &&
                tache.getCreatedBy().getIdUtilisateur().equals(utilisateur.getIdUtilisateur());

        // Condition for admin Italie a ajouter
        if (!isAssignedAdmin && !isSuperAdmin && !isAssignedAdminItalie) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to comment on this task");
        }

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(contenu);
        commentaire.setUtilisateur(utilisateur);
        commentaire.setTache(tache);

        Commentaire savedComment = commentaireRepository.save(commentaire);
        if(isSuperAdmin) {
        for (Utilisateur admin : tache.getAssignedAdmins()) {
            Notification notif = new Notification();
            notif.setNotifLib("Nouveau commentaire sur la tâche");
            notif.setTypeNotif("TASK");
            notif.setUserId(admin.getIdUtilisateur()); // Notify this admin
            notif.setCreatedby(utilisateur.getIdUtilisateur()); // The one who commented
            notif.setMessage("Un nouveau commentaire a été ajouté à la tâche : " + tache.getTitre() + " par " +
                             utilisateur.getPrenom() + " " + utilisateur.getNom());
            notif.setNotificationDate(LocalDateTime.now());
            notif.setReaded(false);

            notifrep.save(notif);
        }
        }else if(!isSuperAdmin) {
        
        Utilisateur superAdmin = utilisateurRepository.findById(1L).get();

        Notification notif1 = new Notification();
        notif1.setNotifLib("Nouveau commentaire sur la tâche");
        notif1.setTypeNotif("TASK");
        notif1.setUserId(superAdmin.getIdUtilisateur()); // Notify this admin
        notif1.setCreatedby(utilisateur.getIdUtilisateur()); // The one who commented
        notif1.setMessage("Un nouveau commentaire a été ajouté à la tâche : " + tache.getTitre() + " par " +
                         utilisateur.getPrenom() + " " + utilisateur.getNom());
        notif1.setNotificationDate(LocalDateTime.now());
        notif1.setReaded(false);

        notifrep.save(notif1);
        }

        return savedComment;    }

    /**
     *
     * @param tacheId
     * @return get all comments for a task
     */

    public List<Commentaire> getCommentaireByTache(Long tacheId) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));
        return commentaireRepository.findByTacheOrderByDateCreationCommentaire(tache);
    }

    /**
     *
     * @param commentId
     * @param userEmail
     * Delete a Commentaire by only the creator
     */
    public void deleteComment(Long commentId, String userEmail){
        Utilisateur utilisateur = utilisateurRepository.findByAdresseMail(userEmail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"user Not found "));

        Commentaire commentaire = commentaireRepository.findById(commentId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment not found"));

        boolean isCommentCreator = commentaire.getUtilisateur().getIdUtilisateur().equals(utilisateur.getIdUtilisateur());

        if (!isCommentCreator){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"User not authorized to delete this comment");
        }

        commentaireRepository.delete(commentaire);
    }
}
