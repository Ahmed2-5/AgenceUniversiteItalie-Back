package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Payement;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tranche;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.service.ClientsService;
import Agence.AgenceUniversiteItalie_backEnd.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "http://localhost:4200")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private ClientsRepository clientsRepository;


    @PostMapping("/ajouterPayment")
    public ResponseEntity<Payement> creePaiement(@RequestBody Map<String, Object> request){
        try {
            Long clientId = Long.valueOf(request.get("clientId").toString());
            BigDecimal montant = new BigDecimal(request.get("montant").toString());
            int nombreTranche = Integer.parseInt(request.get("nombreTranches").toString());


            Clients clients = clientsRepository.findById(clientId).orElseThrow(()-> new RuntimeException("Client non trouver"));

            Payement payement = paiementService.creerPayment(clients, montant, nombreTranche);
            return ResponseEntity.status(HttpStatus.CREATED).body(payement);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Payement>> getPaiementByClient(@PathVariable Long clientId){
        try {
            List<Payement> paiement = paiementService.getPaymentByClient(clientId);
            return ResponseEntity.ok(paiement);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{paiementId}/Tranches")
    public ResponseEntity<List<Tranche>> getTranchesPaiement(@PathVariable Long paiementId){
        try {
            List<Tranche> tranches = paiementService.getTranchesByPayment(paiementId);
            return ResponseEntity.ok(tranches);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // hedhi mtaa eli tbadel el status ahaya
    @PostMapping("/Tranches/{trancheId}/payer")
    public ResponseEntity<Void> payerTranche(@PathVariable Long trancheId){
        try {
            paiementService.reglerTranche(trancheId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{paiementId}/reste")
    public ResponseEntity<BigDecimal> getResteAPayer(@PathVariable Long paiementId) {
        try {
            BigDecimal reste = paiementService.calculerResteAPayer(paiementId);
            return ResponseEntity.ok(reste);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





}
