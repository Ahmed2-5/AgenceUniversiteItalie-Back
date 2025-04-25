package Agence.AgenceUniversiteItalie_backEnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Agence.AgenceUniversiteItalie_backEnd.entity.RDV;
import Agence.AgenceUniversiteItalie_backEnd.service.RDVService;

@RestController
@RequestMapping("/api/RDV")
@CrossOrigin(origins = "http://localhost:4200")
public class RDVController {

	 @Autowired
	    private RDVService rdvService;

	    @PostMapping("/credential/{credentialId}")
	    public ResponseEntity<RDV> addRDVToCredential(
	            @PathVariable Long credentialId,
	            @RequestBody RDV rdv) {
	        try {
	        	RDV savedRDV =
	        			rdvService.addRDVToCredential(credentialId, rdv);
	            return new ResponseEntity<>(savedRDV, HttpStatus.CREATED);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        }
	    }
	    
	    @DeleteMapping("/deleteRdvCredential/{rdvId}")
	    public ResponseEntity<Void> removeRDVFromCredential(
	            @PathVariable Long rdvId) {
	        try {
	        	rdvService.removeRDVFromCredential(rdvId);
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }
	
	    @GetMapping("/getRDVById/{id}")
	    public ResponseEntity<RDV> getRDVById(@PathVariable Long id) {
	        try {
	        	RDV rdv = rdvService.getRDVById(id);
	            return new ResponseEntity<>(rdv, HttpStatus.OK);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }
	   
	    @PutMapping("/updateRDV/{id}")
	    public ResponseEntity<RDV> updateRDV(
	            @PathVariable Long id,
	            @RequestBody RDV rdvDetails) {
	        try {
	        	RDV updatedRdv =
	        			rdvService.updateRDV(id, rdvDetails);
	            return new ResponseEntity<>(updatedRdv, HttpStatus.OK);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }

	    @GetMapping("/RDVs/{credentialId}")
	    public ResponseEntity<Iterable<RDV>> getRDVsByCredentialId(
	            @PathVariable Long credentialId) {
	        try {
	            Iterable<RDV> RDVs =
	            		rdvService.getRDVsByCredentialId(credentialId);
	            return new ResponseEntity<>(RDVs, HttpStatus.OK);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }
	    
	
}
