package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.ClientDocument;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import Agence.AgenceUniversiteItalie_backEnd.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    @Autowired
    private DocumentService documentService ;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    ///////////////////////////Modification houni /////////////////////////////////////////////////////
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientDocument> uploadDocument(
            @RequestParam("file")MultipartFile file,
            @RequestParam("nom") String nom,
            @RequestParam("idClient") Long idClient,
            @RequestParam("idUtilisateur") Long idUtilisateur,
            @RequestParam("authEmail") String authEmail){
        try {
            Utilisateur admin = utilisateurRepository.findByAdresseMail(authEmail)
                    .orElseThrow(()-> new EntityNotFoundException("Utilisateur"));
            return new ResponseEntity<>(
                    documentService.uploadDocument(file,nom,idClient,idUtilisateur,admin),
                    HttpStatus.CREATED);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to upload document: " +e.getMessage());
        }
    }

    @GetMapping("/Client/Documents/{idClient}")
    public ResponseEntity<List<ClientDocument>> getDocumentByClients(@PathVariable Long idClient){
        try {
            return ResponseEntity.ok(documentService.getDocumentByClient(idClient));
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{idDocument}")
    public ResponseEntity<ClientDocument> getDocumentById(@PathVariable Long idDocument){
        try {
            return ResponseEntity.ok(documentService.getDocumentById(idDocument));
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }
    }
    ///////////////////////////Modification houni /////////////////////////////////////////////////////

    @PatchMapping("rename/{idDocument}")
    public ResponseEntity<ClientDocument> renameDocument(@PathVariable Long idDocument,
                                                   @RequestParam String nouveauNom,
                                                   @RequestParam String authEmail){
        try {
            Utilisateur admin = utilisateurRepository.findByAdresseMail(authEmail)
                    .orElseThrow(()-> new EntityNotFoundException("Utilisateur"));
            return ResponseEntity.ok(documentService.updateDocument(idDocument,nouveauNom,admin));
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{idDocument}")
    public ResponseEntity<Map<String,String>> deleteDocument(@PathVariable Long idDocument){
        try {
            documentService.deleteDocument(idDocument);
            return ResponseEntity.ok(Map.of("message","Document deleted Successfully"));
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "failed to delete this document:"+ e.getMessage());
        }
    }

    @GetMapping("/{idDocument}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long idDocument) throws IOException {
        ClientDocument doc = documentService.getDocumentById(idDocument);
        File file = new File(doc.getCheminFichier());

        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        byte[] fileContent = Files.readAllBytes(file.toPath());

        HttpHeaders headers = new HttpHeaders();
        if (doc.getNom().endsWith(".pdf")) {
            headers.setContentType(MediaType.APPLICATION_PDF);  // Set the MIME type to PDF
        } else if (doc.getNom().endsWith(".jpg") || doc.getNom().endsWith(".jpeg") || doc.getNom().endsWith(".png")) {
            headers.setContentType(MediaType.IMAGE_JPEG);  // Set the MIME type to JPEG (or other image types)
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  // Generic binary type for other files
        }

        headers.setContentDispositionFormData("inline", doc.getNom());  // Ensure the file is opened inline

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }



    @PatchMapping(value = "/replace/{idDocument}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientDocument> replaceDocument(
            @PathVariable Long idDocument,
            @RequestParam("file") MultipartFile newFile,
            @RequestParam("newFileName") String newFileName) {
        try {
            // Delegate to the service to handle document replacement with the new file name
            ClientDocument updatedDocument = documentService.replaceDocument(idDocument, newFile, newFileName);
            return ResponseEntity.ok(updatedDocument);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to replace document: " + e.getMessage());
        }
    }

    ///////////////////////////Modification houni /////////////////////////////////////////////////////

    @PutMapping("/{idDOc}/archive")
    public ResponseEntity<?> archiverDoc(@PathVariable Long idDOc,
                                         @RequestParam String authEmail){
        try {
            Utilisateur admin = utilisateurRepository.findByAdresseMail(authEmail)
                    .orElseThrow(()-> new EntityNotFoundException("Utilisateur"));
            return ResponseEntity.ok(documentService.archiveDoc(idDOc,admin));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{idDOc}/unarchive")
    public ResponseEntity<?> unarchiverDoc(@PathVariable Long idDOc){
        try {
            return ResponseEntity.ok(documentService.unarchiveDoc(idDOc));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
