package Agence.AgenceUniversiteItalie_backEnd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Agence.AgenceUniversiteItalie_backEnd.entity.CommentaireCredential;
import Agence.AgenceUniversiteItalie_backEnd.entity.Credential;

public interface CommentaireCredentialRepository extends JpaRepository<CommentaireCredential, Long>{

    List<CommentaireCredential> findByCredentialOrderByDateCreationCommentaireCredential(Credential credential);

}
