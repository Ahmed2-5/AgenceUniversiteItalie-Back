package Agence.AgenceUniversiteItalie_backEnd.repository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Payement;
import Agence.AgenceUniversiteItalie_backEnd.entity.StatusPaiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payement,Long> {

    List<Payement> findByClient_IdClients(Clients clientId);
    List<Payement> findPaymentByStatusPaiment(StatusPaiment status);


}
