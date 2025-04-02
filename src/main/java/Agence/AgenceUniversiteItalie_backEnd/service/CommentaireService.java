package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.Commentaire;
import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tache;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.CommentaireRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.TacheRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentaireService {
    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Add a comment to a task
    public Commentaire addCommentToTache(Long tacheId, String contenu, String userEmail) {
        Utilisateur utilisateur = utilisateurRepository.findByAdresseMail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        // Check if the user is allowed to comment (admin assigned to the task or super admin creator)
        boolean isAssignedAdmin = utilisateur.getRole().getLibelleRole() == EnumRole.ADMIN &&
                tache.getAssignedAdmins().contains(utilisateur);
        boolean isSuperAdmin = utilisateur.getRole().getLibelleRole() == EnumRole.SUPER_ADMIN &&
                tache.getCreatedBy().getIdUtilisateur().equals(utilisateur.getIdUtilisateur());

        if (!isAssignedAdmin && !isSuperAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to comment on this task");
        }

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(contenu);
        commentaire.setUtilisateur(utilisateur);
        commentaire.setTache(tache);

        return commentaireRepository.save(commentaire);
    }
}