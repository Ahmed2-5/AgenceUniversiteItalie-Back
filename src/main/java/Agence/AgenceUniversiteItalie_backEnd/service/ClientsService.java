package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.*;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.CredentialRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.NotificationRepository;
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

    @Autowired
    private NotificationRepository notifrep;
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
       
    // Get full name of the creator
       String createurFullName = createur.getPrenom() + " " + createur.getNom();
       String clientFullName = savedClient.getPrenomClient() + " " + savedClient.getNomClient();

       // 🔔 Notify Assigned Admin Tunisie
       Notification notifAdminTunisie = new Notification();
       notifAdminTunisie.setNotifLib("Nouveau client ajouté");
       notifAdminTunisie.setTypeNotif("CLIENT");
       notifAdminTunisie.setUserId(adminTunisie.getIdUtilisateur());
       notifAdminTunisie.setCreatedby(createur.getIdUtilisateur());
       notifAdminTunisie.setMessage("Un nouveau client (" + clientFullName + ") vous a été assigné par " + createurFullName);
       notifAdminTunisie.setNotificationDate(LocalDateTime.now());
       notifAdminTunisie.setReaded(false);
       notifrep.save(notifAdminTunisie);

       // 🔔 Notify Creator (Super Admin or Admin)
       Notification notifCreateur = new Notification();
       notifCreateur.setNotifLib("Client créé");
       notifCreateur.setTypeNotif("CLIENT");
       notifCreateur.setUserId(createur.getIdUtilisateur());
       notifCreateur.setCreatedby(createur.getIdUtilisateur());
       notifCreateur.setMessage("Un nouveau client : " + clientFullName + " (assigné à " + adminTunisie.getNom() + " " + adminTunisie.getPrenom() + ")"+" Créé par "+ createurFullName);
       notifCreateur.setNotificationDate(LocalDateTime.now());
       notifCreateur.setReaded(false);
       notifrep.save(notifCreateur);

       
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
        
     // 🔔 Notification to Admin Tunisie for the new task
        Notification notif = new Notification();
        notif.setNotifLib("Nouvelle tâche assignée");
        notif.setTypeNotif("TASK");
        notif.setUserId(adminTunisie.getIdUtilisateur());
        notif.setCreatedby(creator.getIdUtilisateur());
        notif.setMessage("Une nouvelle tâche vous a été assignée pour le client : " +
                         client.getNomClient() + " " + client.getPrenomClient());
        notif.setNotificationDate(LocalDateTime.now());
        notif.setReaded(false);
        notifrep.save(notif);
    }



    /**
     *
     * @param idClient
     * @param clientDetails
     * @return Updating the Clients Details
     */
    @Transactional
    public Clients updateClient(Clients clientDetails, Long idClient, String updatedByEmail) {

        Clients client = clientsRepository.findById(idClient)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        EnumTypeService oldService = client.getService(); // Track old service

        // Update basic info
        client.setNomClient(clientDetails.getNomClient());
        client.setPrenomClient(clientDetails.getPrenomClient());
        client.setEmailClient(clientDetails.getEmailClient());
        client.setAdresseClient(clientDetails.getAdresseClient());
        client.setVilleClient(clientDetails.getVilleClient());
        client.setTelephoneClient(clientDetails.getTelephoneClient());
        client.setCodePostale(clientDetails.getCodePostale());
        client.setDateNaissanceClient(clientDetails.getDateNaissanceClient());
        client.setLangue(clientDetails.getLangue());
        client.setService(clientDetails.getService());
        client.setReference(clientDetails.getReference());
        client.setVilleItalie(clientDetails.getVilleItalie());
        client.setProgrammedEtude(clientDetails.getProgrammedEtude());

        if (clientDetails.getAssignedToTunisie() != null) {
            client.setAssignedToTunisie(clientDetails.getAssignedToTunisie());
        }

        if (clientDetails.getAssignedToItalie() != null) {
            client.setAssignedToItalie(clientDetails.getAssignedToItalie());
        }

        if (client.getCredential() != null) {
            client.getCredential().setProgrammeEtude(clientDetails.getProgrammedEtude());
        }

        Clients updatedClient = clientsRepository.save(client);

        // Now handle service change notification
        if (oldService != client.getService()) {
            Utilisateur updatedBy = utilisateurRepository.findByAdresseMail(updatedByEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

            Utilisateur recipient = null;

            if (updatedBy.getRole().getLibelleRole().equals(EnumRole.ADMIN_TUNISIE)) {
                recipient = utilisateurRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Super Admin not found"));
            } else if (updatedBy.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN)) {
                recipient = client.getAssignedToTunisie(); // can be null
            }

            if (recipient != null) {
                Notification notif = new Notification();
                notif.setNotifLib("Changement de service client");
                notif.setTypeNotif("CLIENT");
                notif.setCreatedby(updatedBy.getIdUtilisateur());
                notif.setUserId(recipient.getIdUtilisateur());
                notif.setMessage("Le client " + client.getNomClient() + " " + client.getPrenomClient()
                        + " a changé de service : de " + oldService + " à " + client.getService());
                notif.setNotificationDate(LocalDateTime.now());
                notif.setReaded(false);
                notifrep.save(notif);
            }
        }

        return updatedClient;
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
        Clients updatedClient = clientsRepository.save(client);

        Utilisateur superAdmin = utilisateurRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Super admin not found"));
        // 🔔 Notify the assigned Admin Italie
        Notification notif = new Notification();
        notif.setNotifLib("Affectation d'un client");
        notif.setTypeNotif("CLIENT");
        notif.setUserId(superAdmin.getIdUtilisateur());
        notif.setCreatedby(admin.getIdUtilisateur()); // Optional: if there's a known initiator, set their ID
        notif.setMessage("Le client " + client.getNomClient() + " " + client.getPrenomClient() +
                " a été assigné à l'admin Italie : " + admin.getPrenom() + " " + admin.getNom());
        notif.setNotificationDate(LocalDateTime.now());
        notif.setReaded(false);
        notifrep.save(notif);

        return updatedClient;

    }

    @Transactional
    public Clients removeClientFromAdminItalie(Long clientId, String adminEmail) {
        Clients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        Utilisateur actor = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin Italie or Super Admin not found"));

        Utilisateur adminItalie = client.getAssignedToItalie();
        client.setAssignedToItalie(null);
        Clients updatedClient = clientsRepository.save(client);

        Utilisateur superAdmin = utilisateurRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Super admin not found"));

        Notification notif = new Notification();
        notif.setTypeNotif("CLIENT");
        notif.setNotificationDate(LocalDateTime.now());
        notif.setReaded(false);
        notif.setCreatedby(actor.getIdUtilisateur());

        if (actor.getRole().getLibelleRole().equals(EnumRole.ADMIN_ITALIE)) {
            // Admin Italie removed the client => Notify Super Admin
            notif.setNotifLib("Client désassigné par Admin Italie");
            notif.setUserId(superAdmin.getIdUtilisateur());
            notif.setMessage("L'administrateur Italie " + actor.getPrenom() + " " + actor.getNom() +
                    " a désassigné le client : " + client.getPrenomClient() + " " + client.getNomClient());
        } else if (actor.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN) && adminItalie != null) {
            // Super Admin removed the client => Notify the Admin Italie who was unassigned
            notif.setNotifLib("Client désassigné");
            notif.setUserId(adminItalie.getIdUtilisateur());
            notif.setMessage("Le client " + client.getNomClient() + " " + client.getPrenomClient() +
                    " vous a été désassigné par le Super Admin.");
        }

        notifrep.save(notif);
        return updatedClient;
    }


    @Transactional
    public Clients UpdateAssignClientToAdminTunisie(Long clientId, String adminEmail, String superAdminEmail) {
        Clients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        Utilisateur newAdmin = utilisateurRepository.findByAdresseMail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin Tunisie not found"));

        Utilisateur superAdmin = utilisateurRepository.findByAdresseMail(superAdminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Super Admin not found"));

        if (!newAdmin.getRole().getLibelleRole().equals(EnumRole.ADMIN_TUNISIE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This user is not an Admin Tunisie");
        }

        // 🧠 Save reference to old Admin Tunisie before reassigning
        Utilisateur oldAdmin = client.getAssignedToTunisie();

        // 🔁 Reassign
        client.setAssignedToTunisie(null);
        client.setAssignedToTunisie(newAdmin);
        createAutomaticTaskForClient(client, newAdmin, superAdmin);
        Clients updatedClient = clientsRepository.save(client);

        // 🔔 Notify new assigned Admin Tunisie
        Notification notifNew = new Notification();
        notifNew.setNotifLib("Client assigné");
        notifNew.setTypeNotif("CLIENT");
        notifNew.setUserId(newAdmin.getIdUtilisateur());
        notifNew.setCreatedby(superAdmin.getIdUtilisateur());
        notifNew.setMessage("Vous avez été assigné au client : " +
                            client.getNomClient() + " " + client.getPrenomClient() +
                            " par " + superAdmin.getNom() + " " + superAdmin.getPrenom());
        notifNew.setNotificationDate(LocalDateTime.now());
        notifNew.setReaded(false);
        notifrep.save(notifNew);

        // 🔔 Notify old Admin Tunisie if different from the new one
        if (oldAdmin != null) {
            Notification notifOld = new Notification();
            notifOld.setNotifLib("Client réassigné");
            notifOld.setTypeNotif("CLIENT");
            notifOld.setUserId(oldAdmin.getIdUtilisateur());
            notifOld.setCreatedby(superAdmin.getIdUtilisateur());
            notifOld.setMessage("Le client " + client.getNomClient() + " " + client.getPrenomClient() +
                                " vous a été retiré par le Super Admin et réassigné à " +
                                newAdmin.getNom() + " " + newAdmin.getPrenom());
            notifOld.setNotificationDate(LocalDateTime.now());
            notifOld.setReaded(false);
            notifrep.save(notifOld);
        }

        return updatedClient;
    }













}
