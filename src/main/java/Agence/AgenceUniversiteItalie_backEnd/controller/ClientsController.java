package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.service.ClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Clients")
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
                                          @RequestParam String AssignedAdminEmail) {

        try {
            Clients createdClient = clientsService.clientsCreated(client, adminEmail,AssignedAdminEmail);
            return ResponseEntity.ok(createdClient);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }





}
