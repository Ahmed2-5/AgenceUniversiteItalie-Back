package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.service.ClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/Clients")
@CrossOrigin(origins = "http://localhost:4200")
public class ClientsController {

    @Autowired
    private ClientsService clientsService;

    /**
     *
     * @param client
     * @param adminEmail
     * @return Create the Client
     */
    @PostMapping("/CreateClient")
    public ResponseEntity<?> createClient(@RequestBody Clients client,
                                          @RequestParam String adminEmail,
                                          @RequestParam String assignedAdminTunisie,
                                          @RequestParam String assignedAdminItalie) {

        try {
            Clients createdClient = clientsService.clientsCreated(client, adminEmail,assignedAdminTunisie,assignedAdminItalie);
            return ResponseEntity.ok(createdClient);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping(value = "/UpdateClients/{idClient}")
    public Clients updateClient(@RequestBody Clients clientDetails,
                                          @PathVariable Long idClient){
        return clientsService.updateClient(clientDetails,idClient);
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


    @GetMapping("/assignedTo")
    public ResponseEntity<?> getClientByAssignedTo(@RequestParam String mail){
        try {
            return ResponseEntity.ok(clientsService.getClientByAssignedTo(mail));
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


    @PutMapping("/{idClient}/archive")
    public ResponseEntity<?> archiverClient(@PathVariable Long idClient){
        try {
            return ResponseEntity.ok(clientsService.archiveClient(idClient));
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






}
