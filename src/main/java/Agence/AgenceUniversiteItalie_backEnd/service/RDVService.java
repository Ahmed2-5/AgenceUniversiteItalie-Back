package Agence.AgenceUniversiteItalie_backEnd.service;

import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRendezVous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Agence.AgenceUniversiteItalie_backEnd.entity.Credential;
import Agence.AgenceUniversiteItalie_backEnd.entity.RDV;
import Agence.AgenceUniversiteItalie_backEnd.repository.CredentialRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.RDVRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class RDVService {

	@Autowired
    private RDVRepository rdvRepository;

    @Autowired
    private CredentialRepository credentialRepository;
	
    @Autowired
    private EmailService emailserv;
    
    @Transactional
    public RDV addRDVToCredential(Long credentialId, RDV rdv) {
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new RuntimeException("Credential not found with id: " + credentialId));

        rdv.setCredential(credential);
        RDV savedRdv = rdvRepository.save(rdv);

        // Send notification email to client
        emailserv.envoyerNotificationDajoutNouveauRdv(savedRdv);

        return savedRdv;
    }

    @Transactional
    public void removeRDVFromCredential(Long rdvId) {
        RDV rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new RuntimeException("RDV not found with id: " + rdvId));

        emailserv.envoyerNotificationAnnulationRdv(rdv);
        rdv.setCredential(null);
        rdvRepository.delete(rdv);
    }

    public RDV getRDVById(Long idRDV) {
        return rdvRepository.findById(idRDV)
                .orElseThrow(() -> new RuntimeException("RDV not found with id: " + idRDV));
    }

    public RDV updateRDV(Long idRDV, RDV rdvDetails) {
        RDV rdv = rdvRepository.findById(idRDV)
                .orElseThrow(() -> new RuntimeException("RDV not found with id: " + idRDV));

        rdv.setDateRendezVous(rdvDetails.getDateRendezVous());
        rdv.setEnumRendezVous(rdvDetails.getEnumRendezVous());
        rdv.setTitreRDV(rdvDetails.getTitreRDV());

        RDV updatedRDV = rdvRepository.save(rdv);

        emailserv.envoyerNotificationDeMiseàjourRdv(updatedRDV); // Send update notification

        return updatedRDV;
    }


    public Iterable<RDV> getRDVsByCredentialId(Long credentialId) {
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new RuntimeException("Credential not found with id: " + credentialId));

        return rdvRepository.findByCredential(credential);
    }


    @Scheduled(cron = "0 0 6 * * ?")
    public void envoyerRappelsRDV(){
        LocalDateTime dateRendezVous = LocalDateTime.now().plusDays(1);

        List<RDV> rendezVous = rdvRepository.findRDVByEnumRendezVousAndDateRendezVous(EnumRendezVous.NON_VALIDER, dateRendezVous);

        for(RDV rdv: rendezVous){
            emailserv.envoyerRappelRDV(rdv);
        }
    }



}
