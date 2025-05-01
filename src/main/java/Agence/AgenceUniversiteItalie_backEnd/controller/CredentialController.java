package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.Credential;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import Agence.AgenceUniversiteItalie_backEnd.service.CredentialService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Credential")
@CrossOrigin(origins = "http://localhost:4200")
public class CredentialController {

    @Autowired
    private CredentialService credentialService;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("GetAllCredentials")
    public ResponseEntity<List<Credential>> getAllCredentials() {

        List<Credential> credentials = credentialService.getAllCredentials();
        return new ResponseEntity<>(credentials, HttpStatus.OK);
    }


    @GetMapping("/{credentialId}")
    public ResponseEntity<Credential> getCredentialById(@PathVariable Long credentialId) {

        return credentialService.getCredentialById(credentialId)
                .map(credential -> new ResponseEntity<>(credential, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping("/Clients/{clientId}")
    public ResponseEntity<Credential> getCredentialByClientId(@PathVariable Long clientId) {
        Credential credential = credentialService.getCredentialByClientId(clientId);

        if (credential != null){
            return new ResponseEntity<>(credential, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    ///////////////////////////Modification houni /////////////////////////////////////////////////////

    @PostMapping("/createCredential/{clientId}")
    public ResponseEntity<Credential> createCredential(@PathVariable Long clientId, @RequestBody Credential credential,
                                                       @RequestParam String authEmail) {
        try {
            
            Credential newCredential = credentialService.createCredential(clientId, credential,authEmail);
            return new ResponseEntity<>(newCredential, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    ///////////////////////////Modification houni /////////////////////////////////////////////////////
    @PutMapping("/{credentialId}")
    public ResponseEntity<Credential> updateCredential(@PathVariable Long credentialId, @RequestBody Credential credentialDetails,@RequestParam String updatedByEmail) {
        try {
            Utilisateur admin = utilisateurRepository.findByAdresseMail(updatedByEmail)
                    .orElseThrow(()-> new EntityNotFoundException("Admin not found"));
            Credential updatedCredential = credentialService.updateCredential(credentialId, credentialDetails,updatedByEmail,admin);
            return new ResponseEntity<>(updatedCredential, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/deleteCredential/{credentialId}")
    public ResponseEntity<Void> deleteCredential(@PathVariable Long credentialId) {
        try {
            credentialService.deleteCredentialById(credentialId) ;
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
