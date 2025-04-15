package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.ClientDocument;
import Agence.AgenceUniversiteItalie_backEnd.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientDocument> uploadDocument(
            @RequestParam("file")MultipartFile file,
            @RequestParam("nom") String nom,
            @RequestParam("idClient") Long idClient,
            @RequestParam("idUtilisateur") Long idUtilisateur){
        try {
            return new ResponseEntity<>(
                    documentService.uploadDocument(file,nom,idClient,idUtilisateur),
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

    @PatchMapping("rename/{idDocument}")
    public ResponseEntity<ClientDocument> renameDocument(@PathVariable Long idDocument,
                                                   @RequestParam String nouveauNom){
        try {
            return ResponseEntity.ok(documentService.updateDocument(idDocument,nouveauNom));
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
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", doc.getNom());

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




}
