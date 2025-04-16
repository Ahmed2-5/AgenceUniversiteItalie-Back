package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Payement;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tranche;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.PaymentRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.TrancheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;

@Service
public class PaiementService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TrancheRepository trancheRepository;
    @Autowired
    private ClientsRepository clientsRepository;


    public Payement creerPayment(Clients client , BigDecimal montant , int nombreTranches){
        if (nombreTranches<1 || nombreTranches>5){
            throw new IllegalArgumentException("Nombre de tranches invalide");
        }
        Payement payement = new Payement(client, montant);
        payement.diviserEnTranche(nombreTranches);

        return paymentRepository.save(payement);
    }

    public void reglerTranche(Long idTranche){
        Tranche tranche = trancheRepository.findById(idTranche).orElseThrow(()-> new RuntimeException("Tranche non trouver "));

        tranche.marquerCommePayer();
        trancheRepository.save(tranche);
    }


    public List<Payement> getPaymentByClient(Long clientId){
        Clients clients = clientsRepository.findById(clientId)
                .orElseThrow(()-> new RuntimeException("Client non trouver "));

        return paymentRepository.findByClient_IdClients(clients);
    }

    public List<Tranche> getTranchesByPayment(Long paymentId){

        Payement payement = paymentRepository.findById(paymentId).orElse(null);

        return trancheRepository.findByPayement(payement);
    }











}
