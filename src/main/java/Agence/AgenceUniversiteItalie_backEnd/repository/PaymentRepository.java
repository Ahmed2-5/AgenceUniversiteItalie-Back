package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Payement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payement,Long> {
}
