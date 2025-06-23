package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import Agence.AgenceUniversiteItalie_backEnd.service.ClientsService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;




@RestController
@RequestMapping("/api/Clients")
@CrossOrigin(origins = {"http://localhost:4200",
        "http://universiteitalie.com",
        "https://universiteitalie.com",
        "http://www.universiteitalie.com",
        "https://www.universiteitalie.com"})
public class ClientsController {

    @Autowired
    private ClientsService clientsService;

    @Autowired
    private ClientsRepository clientsRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     *
     * @param client
     * @param adminEmail
     * @return Create the Client
     */
    @PostMapping("/CreateClient")
    public ResponseEntity<?> createClient(@RequestBody Clients client,
                                          @RequestParam String adminEmail,
                                          @RequestParam String assignedAdminTunisie) {


        try {
            Utilisateur admin = utilisateurRepository.findByAdresseMail(adminEmail)
                    .orElseThrow(()-> new EntityNotFoundException("Admin not found"));
            Clients createdClient = clientsService.clientsCreated(client, adminEmail,assignedAdminTunisie,admin);
            return ResponseEntity.ok(createdClient);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(value = "/UpdateClients/{idClient}")
    public Clients updateClient(@RequestBody Clients clientDetails,
                                @PathVariable Long idClient,
                                @RequestParam String updatedByEmail){
        Utilisateur admin = utilisateurRepository.findByAdresseMail(updatedByEmail)
                .orElseThrow(()-> new EntityNotFoundException("Admin not found"));
        return clientsService.updateClient(clientDetails,idClient,updatedByEmail,admin);
    }

    @DeleteMapping("/deleteClient/{idC}")
    public ResponseEntity<?> deleteClient(@PathVariable Long idC,
                                          @RequestParam String adminEmail){
        clientsService.deleteClient(idC,adminEmail);
        return ResponseEntity.ok("Client deleted successfully");
    }


    @GetMapping("/AllClients")
    public ResponseEntity<?> getAllClients(){
        try {
            return ResponseEntity.ok(clientsService.getAllClients());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getclientById/{idClient}")
    public ResponseEntity<?> getClientById(@PathVariable Long idClient){
        try {
            return ResponseEntity.ok(clientsService.findClientById(idClient));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/GetClientNonArchiver")
    public ResponseEntity<?> getClientNonArchiver(){
        try {
            return ResponseEntity.ok(clientsService.getNonArchiveClients());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/GetClientArchiver")
    public ResponseEntity<?> getClientArchiver(){
        try {
            return ResponseEntity.ok(clientsService.getArchivedClients());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/createdBy")
    public ResponseEntity<?> getClientByCreator(@RequestParam String AdminMail){
        try {
            return ResponseEntity.ok(clientsService.getClientsByCreator(AdminMail));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/assignedToTunisie")
    public ResponseEntity<?> getClientByAssignedToTunisie(@RequestParam String mail){
        try {
            return ResponseEntity.ok(clientsService.getClientByAssignedToTunisie(mail));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/assignedToItalie")
    public ResponseEntity<?> getClientByAssignedToItalie(@RequestParam String mail){
        try {
            return ResponseEntity.ok(clientsService.getClientByAssignedToItalie(mail));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchClients(@RequestParam String q ){
        try {
            return ResponseEntity.ok(clientsService.searchClient(q));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

///////////////////////////////// Hedhi sar fiha Modification ////////////////////////////////////////////////
    @PutMapping("/{idClient}/archive")
    public ResponseEntity<?> archiverClient(@PathVariable Long idClient,
                                          @RequestParam  String authEmail){
        try {
            Utilisateur admin = utilisateurRepository.findByAdresseMail(authEmail)
                    .orElseThrow(()-> new EntityNotFoundException("Utilisateur"));
            return ResponseEntity.ok(clientsService.archiveClient(idClient, admin));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{idClient}/Unarchive")
    public ResponseEntity<?> nonArchiver(@PathVariable Long idClient){
        try {
            return ResponseEntity.ok(clientsService.nonArchiver(idClient));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/upload-profile-image/{id}")
    public ResponseEntity<?> uploadProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Ensure user exists
            Clients client = clientsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("client introuvable"));

            // Generate a unique filename
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Define the absolute path for the 'uploads' directory
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");

            // Create the directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Define the full file path
            Path filePath = uploadPath.resolve(filename);
            System.out.println("Uploading file to: " + filePath.toString());  // Debug log

            // Copy the file to the target path
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save the URL to the file in the database
            client.setClientImageUrl(filename);  // Store only the filename
            clientsRepository.save(client);

            return ResponseEntity.ok("Image uploaded successfully. Access at: /uploads/" + filename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        // Define the path to the uploaded file
        Path file = Paths.get("uploads").resolve(filename);
        if (!Files.exists(file)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Determine the file type (content type) dynamically
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        MediaType mediaType = MediaType.IMAGE_JPEG; // Default to JPEG
        if ("png".equals(fileExtension)) {
            mediaType = MediaType.IMAGE_PNG;
        } else if ("gif".equals(fileExtension)) {
            mediaType = MediaType.IMAGE_GIF;
        }

        // Serve the file as a resource
        Resource resource = new FileSystemResource(file.toFile());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @PostMapping("/{clientId}/assign-italie")
    public Clients assignClientToAdminItalie(
            @PathVariable Long clientId,
            @RequestParam String adminEmail) {
        return clientsService.assignClientToAdminItalie(clientId, adminEmail);
    }

    // 📌 Remove client from Admin Italie
    @PostMapping("/{clientId}/remove-italie")
    public Clients removeClientFromAdminItalie(
            @PathVariable Long clientId,
            @RequestParam String adminEmail) {
        return clientsService.removeClientFromAdminItalie(clientId, adminEmail);
    }

    @PostMapping("/{clientId}/updateassign-tunisie")
    public Clients UpdateAssignClientToAdminTunisie(
            @PathVariable Long clientId,
            @RequestParam String adminEmail,
            @RequestParam String superadminEmail
            ) {
        return clientsService.UpdateAssignClientToAdminTunisie(clientId, adminEmail,superadminEmail);
    }


}
