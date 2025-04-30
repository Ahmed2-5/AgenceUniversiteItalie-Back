package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Credential;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CredentialService {

    @Autowired
    private CredentialRepository credentialRepository;


    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private LogActionService logActionService;

    public List<Credential> getAllCredentials() {
        return credentialRepository.findAll();
    }

    public Optional<Credential> getCredentialById(Long idCredential) {
        return credentialRepository.findById(idCredential);
    }

    public Credential getCredentialByClientId(Long clientId) {
        return credentialRepository.findByClientsIdClients(clientId);
    }

    @Transactional
    public Credential createCredential(Long clientId, Credential credential  , Utilisateur admin) {

        Clients clients = clientsRepository.findById(clientId).orElseThrow(()-> new RuntimeException("Client not found"));

        if (clients.getCredential() != null){
            throw new RuntimeException("Credential already exists");
        }

        credential.setClients(clients);
        clients.setCredential(credential);

        Credential savedCredential = credentialRepository.save(credential);
        clientsRepository.save(clients);

       logActionService.ajouterLog(
             "Ajouter Credential for client",
             "Ajout du Credential pour le client" + clients.getNomClient()+ " " + clients.getPrenomClient(),
             "credential",
             savedCredential.getIdCredential(),
             admin
        );


        return savedCredential;
    }

    @Transactional
    public Credential updateCredential(Long credentialId, Credential credentialDetails , Utilisateur admin ) {

        Credential credential = credentialRepository.findById(credentialId).orElseThrow(()-> new RuntimeException("Credential not found"));

        credential.setEmailOutlook(credentialDetails.getEmailOutlook());
        credential.setPasswrodOutlook(credentialDetails.getPasswrodOutlook());
        credential.setEmailGmail(credentialDetails.getEmailGmail());
        credential.setPasswrodGmail(credentialDetails.getPasswrodGmail());
        credential.setPrenotami(credentialDetails.getPrenotami());
        credential.setPasswordPrenotami(credentialDetails.getPasswordPrenotami());
        credential.setProgrammeEtude(credentialDetails.getProgrammeEtude());
        credential.setPreInscrit(credentialDetails.getPreInscrit());
        credential.getClients().setProgrammedEtude(credentialDetails.getProgrammeEtude());


       //return credentialRepository.save(credential);

        Credential CredendialMisAjour = credentialRepository.save(credential);

        logActionService.ajouterLog(
                "Ajouter Credential for client",
                "Ajout du Credential pour le client" + credential.getEmailOutlook() + " " + credential.getPasswrodGmail() ,
                "credential",
                credentialId,
                admin
        );

        return CredendialMisAjour;



    }


    @Transactional
    public void deleteCredentialById(Long credentialId) {
        Credential credential = credentialRepository.findById(credentialId).orElseThrow(()-> new RuntimeException("Credential not found"));

        if (credential.getClients() != null) {
            Clients clients = credential.getClients();
            clients.setCredential(null);
            clientsRepository.save(clients);
        }
        credentialRepository.deleteById(credentialId);
    }
}
