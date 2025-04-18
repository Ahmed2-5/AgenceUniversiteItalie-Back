package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.*;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


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
     *
     */
    @Transactional
    public Clients clientsCreated(Clients clients, String adminEmail,String AdminAssigned){

        Utilisateur createur = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(()-> new EntityNotFoundException("SuperAdmin or Admin with this email" +adminEmail+"is not found"));

       if (!createur.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN) && !createur.getRole().getLibelleRole().equals(EnumRole.ADMIN)){
           throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only Super Admin or Admin can create Clients");
       }

       Utilisateur admin = utilisateurRepository.findByAdresseMail(AdminAssigned)
                       .orElseThrow(()-> new EntityNotFoundException("Admin not found with this adressMail"+AdminAssigned));

       clients.setClientCreatedby(createur);
       clients.setAssignedTo(admin);
       return clientsRepository.save(clients);
    }

    /**
     *
     * @param idClient
     * @param clientDetails
     * @return Updating the Clients Details
     */
    @Transactional
    public Clients updateClient(Clients clientDetails ,Long idClient){

        Clients clients = clientsRepository.findById(idClient)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"client not found"));

        clients.setNomClient(clientDetails.getNomClient());
        clients.setPrenomClient((clientDetails.getPrenomClient()));
        clients.setEmailClient(clientDetails.getEmailClient());
        clients.setAdresseClient(clientDetails.getAdresseClient());
        clients.setVilleClient(clientDetails.getVilleClient());
        clients.setTelephoneClient(clientDetails.getTelephoneClient());
        clients.setCodePostale(clientDetails.getCodePostale());
        clients.setDateNaissanceClient(clientDetails.getDateNaissanceClient());
        clients.setLangue(clientDetails.getLangue());
        clients.setService(clientDetails.getService());
        clients.setReference(clientDetails.getReference());

        if(clientDetails.getAssignedTo() !=null){ clients.setAssignedTo(clientDetails.getAssignedTo());}

        return clientsRepository.save(clients);
    }

    /**
     *
     * @param idC
     * Delete a client
     */
    public void deleteClient(Long idC, String SuperAdminEmail){
        Utilisateur admin = utilisateurRepository.findByAdresseMail(SuperAdminEmail)
                .orElseThrow(()-> new EntityNotFoundException("SuperAdmin or Admin with this email" +SuperAdminEmail+"is not found"));

        Clients clientSupp = clientsRepository.findById(idC)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"le client n'est pas trouver "));

    boolean AdminCreator = admin.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN);
    boolean isAssignedTo = clientSupp.getAssignedTo().getIdUtilisateur().equals(admin.getIdUtilisateur());

    if (!isAssignedTo && !AdminCreator){
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Only Super Admin or Admin can create Clients");
    }


        clientsRepository.delete(clientSupp);
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

        if (client.getArchive() == Archive.ARCHIVER){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ce client est deja archiver");
        }

        client.setArchive(Archive.ARCHIVER);
        return clientsRepository.save(client);
    }

    /**
     *
     * @param idClient
     * @return Non-Archiver client
     */
    @Transactional
    public Clients nonArchiver(Long idClient){
        Clients clientArchiver = clientsRepository.findById(idClient)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Client not found"));

        if (clientArchiver.getArchive() == Archive.NON_ARCHIVER){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST," ce client est deja Non archiver");
        }

        clientArchiver.setArchive(Archive.NON_ARCHIVER);
        return clientsRepository.save(clientArchiver);
    }


    public List<Clients> getAllClients(){
        return clientsRepository.findAll();
    }

    public Clients findClientById(Long idClient){
        return clientsRepository.findById(idClient)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Le client n'est pas trouver"));
    }

    //get client eli mahomech archiver
    public List<Clients> getNonArchiveClients(){
        return clientsRepository.findByArchive(Archive.NON_ARCHIVER);
    }


    public List<Clients> getArchivedClients(){
        return clientsRepository.findByArchive(Archive.ARCHIVER);
    }


    //Admin eli aamal el creation mtaa el client
    public List<Clients> getClientsByCreator(String AdressMail){
        Utilisateur idAdmin = utilisateurRepository.findByAdresseMail(AdressMail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"l'Admin ou le Super Admin n'est pas trouver"));

        return clientsRepository.findClientsByClientCreatedby(idAdmin);
    }


    //Admin eli lehi bel client
    public List<Clients> getClientByAssignedTo(String adresseMail){

        Utilisateur adminEmail = utilisateurRepository.findByAdresseMail(adresseMail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND," l'admin est introvable"));
        return clientsRepository.findClientsByAssignedTo(adminEmail);

    }


    //Search lel client bel nom et prenom
    public List<Clients> searchClient(String searchTerm){
        return clientsRepository.searchClients(searchTerm);
    }
















}
