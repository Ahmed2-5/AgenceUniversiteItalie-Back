package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Payement;
import Agence.AgenceUniversiteItalie_backEnd.entity.StatusTranche;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tranche;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.PaymentRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.TrancheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaiementService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TrancheRepository trancheRepository;
    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private EmailService emailService;


    @Transactional
    public Payement creerPayment(Clients client , BigDecimal montant , int nombreTranches){
        if (nombreTranches<1 || nombreTranches>5){
            throw new IllegalArgumentException("Nombre de tranches invalide");
        }
        Payement payement = new Payement(client, montant);
        payement.diviserEnTranche(nombreTranches);

        return paymentRepository.save(payement);
    }

    @Transactional
    public void reglerTranche(Long idTranche){
        Tranche tranche = trancheRepository.findById(idTranche).orElseThrow(()-> new RuntimeException("Tranche non trouver "));

        tranche.marquerCommePayer();
        trancheRepository.save(tranche);
    }


    public List<Payement> getPaymentByClient(Long clientId){
        return paymentRepository.findByClientIdClients(clientId);
    }

    public List<Tranche> getTranchesByPayment(Long paymentId){
        return trancheRepository.findByPayementIdPayement(paymentId);
    }


    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void verifierEcheances(){
        LocalDate today = LocalDate.now();

        List<Tranche> tranches = trancheRepository.findByStatusTrancheAndDateLimiteLessThanEqual(StatusTranche.EN_ATTENTE, today);

        for (Tranche tranche : tranches) {
            tranche.setStatusTranche(StatusTranche.EN_RETARD);


            if (!tranche.isNotificationRetardEnvoyee()){
                emailService.envoyerNotificationRetard(tranche);
                tranche.setNotificationRetardEnvoyee(true);
            }

            trancheRepository.save(tranche);
        }
    }


    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void envoyerRappels(){
        LocalDate dateRappel = LocalDate.now().plusDays(3);

        List<Tranche> tranches = trancheRepository.findByStatusTrancheAndDateLimite(StatusTranche.EN_ATTENTE, dateRappel);

        for (Tranche tranche: tranches){
            if (!tranche.isNotificationEnvoyee()){
                emailService.envoyerRappelEcheance(tranche);
                tranche.setNotificationEnvoyee(true);
                trancheRepository.save(tranche);
            }
        }
    }
    
    public BigDecimal calculerResteAPayer(Long paiementId) {
        List<Tranche> tranches = getTranchesByPayment(paiementId);

        return tranches.stream()
                .filter(t -> t.getStatusTranche() == StatusTranche.EN_ATTENTE || t.getStatusTranche() == StatusTranche.EN_RETARD)
                .map(Tranche::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }















}
