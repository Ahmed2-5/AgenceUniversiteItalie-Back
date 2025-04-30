package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.*;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.PaymentRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.TrancheRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private LogActionService logActionService;


    @Transactional
    public Payement creerPayment(Clients client , BigDecimal montant , int nombreTranches , Utilisateur admin ){
        if (nombreTranches<1 || nombreTranches>5){
            throw new IllegalArgumentException("Nombre de tranches invalide");
        }
        Payement payement = new Payement(client, montant);
        payement.diviserEnTranche(nombreTranches);

        //return paymentRepository.save(payement);


        Payement savedPayement = paymentRepository.save(payement);

        logActionService.ajouterLog(
                "Creation paiement",
                "Paiement créé pour le client :" + client.getNomClient() + " " + client.getPrenomClient() +
                        " - Montant: " + montant + " - Nombre de tranches: " + nombreTranches,
                "paiement",
                savedPayement.getIdPayement(),
                admin

        );

        return savedPayement;
    }

    @Transactional
    public void reglerTranche(Long idTranche , Utilisateur admin ){
        Tranche tranche = trancheRepository.findById(idTranche).orElseThrow(()-> new RuntimeException("Tranche non trouver "));

        Payement payement = tranche.getPayement();
        Clients clients = payement.getClient();
        int numeroTranche = tranche.getNumero();
        BigDecimal montantTranche = tranche.getMontant();

        tranche.marquerCommePayer();
        trancheRepository.save(tranche);

        logActionService.ajouterLog(
                "Réglement tranche",
                "Tranche" + numeroTranche + " réglée pour le client : " + clients.getNomClient() + " " + clients.getPrenomClient() + " -Montant: " + montantTranche,
                "Paiement",
                idTranche,
                admin
        );

    }


    // only for admin Tunisie
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
    
    @Transactional
    public BigDecimal calculerResteAPayer(Long paiementId) {
        List<Tranche> tranches = getTranchesByPayment(paiementId);

        return tranches.stream()
                .filter(t -> t.getStatusTranche() == StatusTranche.EN_ATTENTE || t.getStatusTranche() == StatusTranche.EN_RETARD)
                .map(Tranche::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateMontantTrancheEtRedistribuer(Long idTranche , BigDecimal nouveauMontant , String userEmail) {
        Utilisateur utilisateur = utilisateurRepository.findByAdresseMail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur Non trouver"));

        EnumRole role = utilisateur.getRole().getLibelleRole();
        if (role != EnumRole.SUPER_ADMIN && role != EnumRole.ADMIN_TUNISIE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have the permission");
        }

        Tranche tranche = trancheRepository.findById(idTranche)
                .orElseThrow(() -> new RuntimeException("La tranche est introvable"));

        Payement payement = tranche.getPayement();

        tranche.setMontant(nouveauMontant);
        tranche.setMontantFixe(true); // ✅ Mark this tranche as fixed
        trancheRepository.save(tranche);

        List<Tranche> autresTranches = payement.getTranches().stream()
                .filter(t -> !t.getIdTranche().equals(idTranche))
                .filter(t -> !t.isMontantFixe()) // ✅ Only redistribute non-fixed tranches
                .sorted((t1, t2) -> Integer.compare(t1.getNumero(), t2.getNumero()))
                .toList();

        BigDecimal montantTotalFixe = payement.getTranches().stream()
                .filter(Tranche::isMontantFixe)
                .map(Tranche::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRestant = payement.getMontantaTotal().subtract(montantTotalFixe);

        int nbTranchesRestantes = autresTranches.size();
        if (nbTranchesRestantes > 0) {
            BigDecimal montantParTranche = totalRestant.divide(BigDecimal.valueOf(nbTranchesRestantes), 2, RoundingMode.HALF_UP);
            BigDecimal cumul = BigDecimal.ZERO;

            for (int i = 0; i < nbTranchesRestantes; i++) {
                Tranche t = autresTranches.get(i);
                if (i == nbTranchesRestantes - 1) {
                    t.setMontant(totalRestant.subtract(cumul));
                } else {
                    t.setMontant(montantParTranche);
                    cumul = cumul.add(montantParTranche);
                }
                trancheRepository.save(t);
            }
        }

        List<Tranche> tranchesToDelete = payement.getTranches().stream()
                .filter(t -> t.getMontant() != null && t.getMontant().compareTo(BigDecimal.ZERO) == 0)
                .toList();

        if (!tranchesToDelete.isEmpty()) {
            trancheRepository.deleteAll(tranchesToDelete);
            payement.getTranches().removeAll(tranchesToDelete); // optional but keeps object model clean
        }
        
        payement.mettreAJourLeReste();
        paymentRepository.save(payement);
    }






}
