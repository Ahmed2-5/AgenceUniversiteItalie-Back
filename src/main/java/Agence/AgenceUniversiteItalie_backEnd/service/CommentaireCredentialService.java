package Agence.AgenceUniversiteItalie_backEnd.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import Agence.AgenceUniversiteItalie_backEnd.entity.Commentaire;
import Agence.AgenceUniversiteItalie_backEnd.entity.CommentaireCredential;
import Agence.AgenceUniversiteItalie_backEnd.entity.Credential;
import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Notification;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tache;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.CommentaireCredentialRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.CommentaireRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.CredentialRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.NotificationRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;

@Service
public class CommentaireCredentialService {

	@Autowired
    private CommentaireCredentialRepository commentaireCredentialRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationRepository notifrep;
    
 // Add a comment to a credential
    public CommentaireCredential addCommentToCredential(Long credentialId, String contenu, String userEmail) {
        Utilisateur utilisateur = utilisateurRepository.findByAdresseMail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "credential not found"));

        boolean isAssignedAdmin = utilisateur.getRole().getLibelleRole() == EnumRole.ADMIN_TUNISIE &&
                credential.getClients().getAssignedToTunisie().equals(utilisateur);

        boolean isAssignedAdminItalie = utilisateur.getRole().getLibelleRole() == EnumRole.ADMIN_ITALIE &&
                credential.getClients().getAssignedToItalie().equals(utilisateur);

        boolean isSuperAdmin = utilisateur.getRole().getLibelleRole() == EnumRole.SUPER_ADMIN;

        // Condition for admin Italie a ajouter
        if (!isAssignedAdmin && !isSuperAdmin && !isAssignedAdminItalie) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to comment on this credential");
        }

        CommentaireCredential commentaire = new CommentaireCredential();
        commentaire.setContenuCommentaireCredential(contenu);
        commentaire.setUtilisateur(utilisateur);
        commentaire.setCredential(credential);

        CommentaireCredential savedComment = commentaireCredentialRepository.save(commentaire);
        

        if(isSuperAdmin) {
                Notification notif = new Notification();
                notif.setNotifLib("Nouveau commentaire");
                notif.setTypeNotif("CLIENT");
                notif.setUserId(credential.getClients().getAssignedToItalie().getIdUtilisateur()); // Notify this admin
                notif.setCreatedby(utilisateur.getIdUtilisateur()); // The one who commented
                notif.setMessage("Un nouveau commentaire a été ajouté à la credential du client : " + credential.getClients().getPrenomClient()+ " " + credential.getClients().getNomClient() + " par " +
                                 utilisateur.getPrenom() + " " + utilisateur.getNom());
                notif.setNotificationDate(LocalDateTime.now());
                notif.setReaded(false);
                notifrep.save(notif);
                
                Notification notif2 = new Notification();
                notif2.setNotifLib("Nouveau commentaire");
                notif2.setTypeNotif("CLIENT");
                notif2.setUserId(credential.getClients().getAssignedToTunisie().getIdUtilisateur()); // Notify this admin
                notif2.setCreatedby(utilisateur.getIdUtilisateur()); // The one who commented
                notif2.setMessage("Un nouveau commentaire a été ajouté à la credential du client : " + credential.getClients().getPrenomClient()+ " " + credential.getClients().getNomClient() + " par " +
                        utilisateur.getPrenom() + " " + utilisateur.getNom());
                notif2.setNotificationDate(LocalDateTime.now());
                notif2.setReaded(false);
                notifrep.save(notif2);
            
            }else if(isAssignedAdminItalie) {
            
            Notification notif1 = new Notification();
            notif1.setNotifLib("Nouveau commentaire");
            notif1.setTypeNotif("CLIENT");
            notif1.setUserId(credential.getClients().getAssignedToTunisie().getIdUtilisateur()); // Notify this admin
            notif1.setCreatedby(utilisateur.getIdUtilisateur()); // The one who commented
            notif1.setMessage("Un nouveau commentaire a été ajouté à la credential du client : " + credential.getClients().getPrenomClient()+ " " + credential.getClients().getNomClient() + " par " +
                    utilisateur.getPrenom() + " " + utilisateur.getNom());
            notif1.setNotificationDate(LocalDateTime.now());
            notif1.setReaded(false);

            notifrep.save(notif1);
            }else if(isAssignedAdmin) {
                
                Notification notif3 = new Notification();
                notif3.setNotifLib("Nouveau commentaire");
                notif3.setTypeNotif("CLIENT");
                notif3.setUserId(credential.getClients().getAssignedToItalie().getIdUtilisateur()); // Notify this admin
                notif3.setCreatedby(utilisateur.getIdUtilisateur()); // The one who commented
                notif3.setMessage("Un nouveau commentaire a été ajouté à la credential du client : " + credential.getClients().getPrenomClient()+ " " + credential.getClients().getNomClient() + " par " +
                        utilisateur.getPrenom() + " " + utilisateur.getNom());
                notif3.setNotificationDate(LocalDateTime.now());
                notif3.setReaded(false);

                notifrep.save(notif3);
                }
        
        return savedComment;    
        }
    
   
    
    public List<CommentaireCredential> getCommentaireByCredential(Long credentialId) {
    	Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Credential not found"));
        return commentaireCredentialRepository.findByCredentialOrderByDateCreationCommentaireCredential(credential);
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

        CommentaireCredential commentaire = commentaireCredentialRepository.findById(commentId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment not found"));

        boolean isCommentCreator = commentaire.getUtilisateur().getIdUtilisateur().equals(utilisateur.getIdUtilisateur());

        if (!isCommentCreator){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"User not authorized to delete this comment");
        }

        commentaireCredentialRepository.delete(commentaire);
    }
}
