package Agence.AgenceUniversiteItalie_backEnd.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Agence.AgenceUniversiteItalie_backEnd.entity.CommentaireCredential;
import Agence.AgenceUniversiteItalie_backEnd.service.CommentaireCredentialService;

@RestController
@RequestMapping("/api/commentaireCredential")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentaireCredentialController {

	@Autowired
    CommentaireCredentialService commentaireCredentialService;

    @PostMapping("/addCommentaire")
    public ResponseEntity<?> ajouterCommentaire(@RequestBody Map<String,String> commentRequest, @RequestParam String userEmail){
        try {
            Long credentialId = Long.parseLong(commentRequest.get("credentialId"));
            String contenu = commentRequest.get("contenu");

            CommentaireCredential commentaire = commentaireCredentialService.addCommentToCredential(credentialId,contenu,userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentaire);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/credential/{credentialId}")
    public ResponseEntity<?> GetCommentsByCredential(@PathVariable Long credentialId){
        try {
            List<CommentaireCredential> commentaires= commentaireCredentialService.getCommentaireByCredential(credentialId);
            return ResponseEntity.ok(commentaires);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

  
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deletComment(@PathVariable Long commentId,
                                          @RequestParam String userEmail){
        try {
        	commentaireCredentialService.deleteComment(commentId,userEmail);
            return ResponseEntity.ok("comment deleted successfuly");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
