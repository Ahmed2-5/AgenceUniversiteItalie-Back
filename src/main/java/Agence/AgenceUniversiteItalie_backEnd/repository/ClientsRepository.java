package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientsRepository extends JpaRepository<Clients, Long> {
}
