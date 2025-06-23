package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.Commentaire;
import Agence.AgenceUniversiteItalie_backEnd.service.CommentaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commentaire")
@CrossOrigin(origins = {"http://localhost:4200",
        "http://universiteitalie.com",
        "https://universiteitalie.com",
        "http://www.universiteitalie.com",
        "https://www.universiteitalie.com"})
public class CommentaireController {

    @Autowired
    CommentaireService commentaireService;

    @PostMapping("/addCommentaire")
    public ResponseEntity<?> ajouterCommentaire(@RequestBody Map<String,String> commentRequest, @RequestParam String userEmail){
        try {
            Long tacheId = Long.parseLong(commentRequest.get("tacheId"));
            String contenu = commentRequest.get("contenu");

            Commentaire commentaire = commentaireService.addCommentToTache(tacheId,contenu,userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentaire);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    /**
     *
     * @param tacheId
     * @return all commentes related to the taches
     */

    @GetMapping("/tache/{tacheId}")
    public ResponseEntity<?> GetCommentsByTache(@PathVariable Long tacheId){
        try {
            List<Commentaire> commentaires= commentaireService.getCommentaireByTache(tacheId);
            return ResponseEntity.ok(commentaires);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     *
     * @param commentId
     * @param userEmail
     * @return delete commentaire.
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deletComment(@PathVariable Long commentId,
                                          @RequestParam String userEmail){
        try {
            commentaireService.deleteComment(commentId,userEmail);
            return ResponseEntity.ok("comment deleted successfuly");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
