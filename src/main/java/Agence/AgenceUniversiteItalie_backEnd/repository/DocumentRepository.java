package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Long> {
}
