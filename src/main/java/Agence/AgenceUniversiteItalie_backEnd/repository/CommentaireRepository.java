package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Commentaire;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    List<Commentaire> findByTacheOrderByDateCreationCommentaire(Tache tache);

}
