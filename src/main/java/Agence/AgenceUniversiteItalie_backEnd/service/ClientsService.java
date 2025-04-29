package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.*;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.CredentialRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.TacheRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ClientsService {

    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private TacheRepository tacheRepository;

    /**
     *
     * @param clients
     * @param adminEmail
     * @return Create a client pour gerer ses dossiers et tous
     *
     */
    @Transactional
    public Clients clientsCreated(Clients clients, String adminEmail,String adminAssignedTunisie){

        Utilisateur createur = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(()-> new EntityNotFoundException("SuperAdmin or Admin with this email" +adminEmail+"is not found"));

       if (!createur.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN) && !createur.getRole().getLibelleRole().equals(EnumRole.ADMIN_TUNISIE)){
           throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only Super Admin or Admin can create Clients");
       }

       Utilisateur adminTunisie = utilisateurRepository.findByAdresseMail(adminAssignedTunisie)
                       .orElseThrow(()-> new EntityNotFoundException("Admin not found with this adressMail"+adminAssignedTunisie));

     //  Utilisateur adminItalie = utilisateurRepository.findByAdresseMail(adminAssignedItalie)
     //          .orElseThrow(()-> new EntityNotFoundException("admin italie not found"+ adminAssignedItalie));

       Credential emptyCredential = new Credential();
       emptyCredential.setProgrammeEtude(clients.getProgrammedEtude());
       credentialRepository.save(emptyCredential); 

       clients.setClientCreatedby(createur);
       clients.setAssignedToTunisie(adminTunisie);
      // clients.setAssignedToItalie(adminItalie);
       clients.setCredential(emptyCredential);

       emptyCredential.setClients(clients);
       Clients savedClient = clientsRepository.save(clients);
       createAutomaticTaskForClient(savedClient,adminTunisie,createur);
       return savedClient;
    }

    private void createAutomaticTaskForClient(Clients client, Utilisateur adminTunisie, Utilisateur creator) {
        Tache task = new Tache();
        task.setTitre("Tâche à faire pour le client '" + client.getNomClient() + " " + client.getPrenomClient() + "'");

        StringBuilder description = new StringBuilder();
        description.append("* Création mail, création compte prenotami et aussi compte université Italie pour ")
                   .append(client.getNomClient()).append(" ").append(client.getPrenomClient())
                   .append("<br>") // HTML line break
                   .append("- mail: ").append(client.getEmailClient())
                   .append("<br>") // HTML line break
                   .append("- Téléphone: ").append(client.getTelephoneClient());

        task.setDescription(description.toString());
        task.setPriority(EnumPriority.Elevée);
        task.setStatus(EnumStatutTache.PAS_ENCORE);
        task.setCreatedBy(creator);
        task.setDueDate(LocalDateTime.now().plusHours(24));
        task.getAssignedAdmins().add(adminTunisie);

        tacheRepository.save(task);
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
        clients.setVilleItalie(clientDetails.getVilleItalie());
        clients.setProgrammedEtude(clientDetails.getProgrammedEtude());
        if(clientDetails.getAssignedToTunisie() !=null){ clients.setAssignedToTunisie(clientDetails.getAssignedToTunisie());}
        if (clientDetails.getAssignedToItalie() != null){clients.setAssignedToItalie(clientDetails.getAssignedToItalie());}
        clients.getCredential().setProgrammeEtude(clientDetails.getProgrammedEtude());
        return clientsRepository.save(clients);
    }

    /**
     *
     * @param idC
     * Delete a client
     */
    @Transactional
    public void deleteClient(Long idC, String superAdminEmail) {
        Utilisateur admin = utilisateurRepository.findByAdresseMail(superAdminEmail)
                .orElseThrow(() -> new EntityNotFoundException("SuperAdmin or Admin with this email " + superAdminEmail + " is not found"));

        Clients clientSupp = clientsRepository.findById(idC)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client n'est pas trouvé"));

        boolean isSuperAdmin = admin.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN);
        boolean isAssignedToTunisie = clientSupp.getAssignedToTunisie() != null &&
                                       clientSupp.getAssignedToTunisie().getIdUtilisateur().equals(admin.getIdUtilisateur());

        if (!isSuperAdmin && !isAssignedToTunisie) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Super Admin or assigned Admin can delete Clients");
        }

        // 🔥 Break the bidirectional link to Credential
        Credential credential = clientSupp.getCredential();
        if (credential != null) {
            clientSupp.setCredential(null);
            credential.setClients(null);
            credentialRepository.delete(credential);
        }

        // 🧾 Clear payments (if needed)
        clientSupp.getPayementClient().forEach(payement -> payement.setClient(null));
        clientSupp.getPayementClient().clear();

        // 📎 Clear documents
        clientSupp.getDocuments().forEach(doc -> doc.setClientDocument(null));
        clientSupp.getDocuments().clear();

        // 👥 Break admin assignments (optional but safe)
        clientSupp.setAssignedToTunisie(null);
        clientSupp.setAssignedToItalie(null);
        clientSupp.setClientCreatedby(null);

        // 🧨 Finally delete the client
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
    public List<Clients> getClientByAssignedToItalie(String adresseMail){

        Utilisateur adminEmail = utilisateurRepository.findByAdresseMail(adresseMail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND," l'admin est introvable"));
        return clientsRepository.findClientsByAssignedToItalie(adminEmail);

    }

    public List<Clients> getClientByAssignedToTunisie(String adresseMail){

        Utilisateur adminEmail = utilisateurRepository.findByAdresseMail(adresseMail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND," l'admin est introvable"));
        return clientsRepository.findClientsByAssignedToTunisie(adminEmail);

    }

    //Search lel client bel nom et prenom
    public List<Clients> searchClient(String searchTerm){
        return clientsRepository.searchClients(searchTerm);
    }


    @Transactional
    public Clients assignClientToAdminItalie(Long clientId, String adminEmail) {
        Clients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        Utilisateur admin = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin Italie not found"));

        if (!admin.getRole().getLibelleRole().equals(EnumRole.ADMIN_ITALIE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This user is not an Admin Italie");
        }

        client.setAssignedToItalie(admin);
        return clientsRepository.save(client);
    }

    @Transactional
    public Clients removeClientFromAdminItalie(Long clientId, String adminEmail) {
        Clients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        Utilisateur admin = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin Italie not found"));

        if (!admin.getRole().getLibelleRole().equals(EnumRole.ADMIN_ITALIE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This user is not an Admin Italie");
        }

        if (client.getAssignedToItalie() == null || !client.getAssignedToItalie().getIdUtilisateur().equals(admin.getIdUtilisateur())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This client is not assigned to the given Admin Italie");
        }

        client.setAssignedToItalie(null);
        return clientsRepository.save(client);
    }














}
