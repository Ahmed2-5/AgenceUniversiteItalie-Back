package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.Archive;
import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClientsService {

    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;


    /**
     *
     * @param clients
     * @param adminEmail
     * @return Create a client pour gerer ses dossiers et tous
     */
    @Transactional
    public Clients clientsCreated(Clients clients, String adminEmail){

        Utilisateur createur = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(()-> new EntityNotFoundException("SuperAdmin or Admin with this email" +adminEmail+"is not found"));

       if (!createur.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN) && !createur.getRole().getLibelleRole().equals(EnumRole.ADMIN)){
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Only Super Admin or Admin can create Clients");
       }

       clients.setClientCreatedby(createur);
       return clientsRepository.save(clients);
    }

    /**
     *
     * @param idClient
     * @param clientDetails
     * @return Updating the Clients Details
     */
    @Transactional
    public Clients updateClient(Long idClient, Clients clientDetails){

        Clients clients = clientsRepository.findById(idClient)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"client not found"));

        clients.setNomClient(clientDetails.getNomClient());
        clients.setPrenomClient((clientDetails.getPrenomClient()));
        clients.setEmailClient(clientDetails.getEmailClient());
        clients.setAdresseClient(clientDetails.getAdresseClient());
        clients.setVilleClient(clients.getVilleClient());
        clients.setTelephoneClient(clients.getTelephoneClient());
        clients.setCodePostale(clients.getCodePostale());
        clients.setDateNaissanceClient(clientDetails.getDateNaissanceClient());
        clients.setLangue(clientDetails.getLangue());

        return clientsRepository.save(clients);
    }

    /**
     *
     * @param idClient
     * @return archiver un clients
     */
    @Transactional
    public Clients archiveClient(Long idClient){
        Clients client = clientsRepository.findById(idClient)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"ce Client est n'est pas trouver"));

        // manque d'une
        client.setArchive(Archive.ARCHIVER);
        return clientsRepository.save(client);
    }



}
