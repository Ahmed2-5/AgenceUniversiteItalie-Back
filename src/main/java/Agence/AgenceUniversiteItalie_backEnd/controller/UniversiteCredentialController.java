package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.UniversiteCredential;
import Agence.AgenceUniversiteItalie_backEnd.service.UniversiteCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Universite-Credential")
public class UniversiteCredentialController {

    @Autowired
    private UniversiteCredentialService universiteCredentialService;

    @PostMapping("/credential/{credentialId}")
    public ResponseEntity<UniversiteCredential> addUniversiteCredentialToCredential(
            @PathVariable Long credentialId,
            @RequestBody UniversiteCredential universiteCredential) {
        try {
            UniversiteCredential savedUniversiteCredential =
                    universiteCredentialService.addUniversiteCredentialToCredential(credentialId, universiteCredential);
            return new ResponseEntity<>(savedUniversiteCredential, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteUniversiteCredential/{universiteCredentialId}")
    public ResponseEntity<Void> removeUniversiteCredentialFromCredential(
            @PathVariable Long universiteCredentialId) {
        try {
            universiteCredentialService.removeUniversiteCredentialFromCredential(universiteCredentialId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getUniversiteCredentialById/{id}")
    public ResponseEntity<UniversiteCredential> getUniversiteCredentialById(@PathVariable Long id) {
        try {
            UniversiteCredential universiteCredential = universiteCredentialService.getUniversiteCredentialById(id);
            return new ResponseEntity<>(universiteCredential, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/UpdateUniversiteCredential/{id}")
    public ResponseEntity<UniversiteCredential> updateUniversiteCredential(
            @PathVariable Long id,
            @RequestBody UniversiteCredential universiteCredentialDetails) {
        try {
            UniversiteCredential updatedUniversiteCredential =
                    universiteCredentialService.updateUniversiteCredential(id, universiteCredentialDetails);
            return new ResponseEntity<>(updatedUniversiteCredential, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // hedi eli bich tekhdem biha normalement fel get ya tfol
    @GetMapping("/credential/{credentialId}")
    public ResponseEntity<Iterable<UniversiteCredential>> getUniversiteCredentialsByCredentialId(
            @PathVariable Long credentialId) {
        try {
            Iterable<UniversiteCredential> universiteCredentials =
                    universiteCredentialService.getUniversiteCredentialsByCredentialId(credentialId);
            return new ResponseEntity<>(universiteCredentials, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

