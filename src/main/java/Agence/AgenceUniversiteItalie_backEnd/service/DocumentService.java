package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.ClientDocument;
import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.ClientsRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.DocumentRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;


    public ClientDocument getDocumentById(Long idDocument){
        return documentRepository.findById(idDocument).
                orElseThrow(()->new EntityNotFoundException("Documents not found whith this id"+idDocument));
    }

    public List<ClientDocument> getDocumentByClient(Long idClient){
        return documentRepository.findByClientDocument_IdClients(idClient);
    }

    /**
     *
     * @param file
     * @param nom
     * @param idClient
     * @param idUtilisateur
     * @return Ajouter un Document a un utilisateur
     * @throws IOException
     */
    @Transactional
    public ClientDocument uploadDocument(MultipartFile file , String nom , Long idClient, Long idUtilisateur)throws IOException{
        Clients clients = clientsRepository.findById(idClient)
                .orElseThrow(()-> new EntityNotFoundException("Cllient not found with this id:"+idClient));

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(()-> new EntityNotFoundException("this Admin not found"+idUtilisateur));

        EnumRole role = utilisateur.getRole().getLibelleRole();
        if (role != EnumRole.SUPER_ADMIN && role != EnumRole.ADMIN){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"this Admin est n'est pas autorise");
        }

        String clientDir = uploadDir + "/" + idClient;
        Path uploadPath = Paths.get(clientDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String OriginalFilename = file.getOriginalFilename();
        String fileExtension = OriginalFilename != null ? OriginalFilename.substring(OriginalFilename.lastIndexOf(".")) : ".pdf";
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(),filePath);

        ClientDocument document = new ClientDocument();
        document.setNom(nom);
        document.setCheminFichier(clientDir + "/" +uniqueFileName );
        document.setClientDocument(clients);
        document.setAjouterPar(utilisateur);
        document.setDateAjout(LocalDateTime.now());

        return documentRepository.save(document);

    }


    @Transactional
    public ClientDocument updateDocument(Long idDoc, String nouveauNom){
        ClientDocument document = documentRepository.findById(idDoc)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "le document n'est pas trouver"));

        // Extract extension from the current file name
        String extension = "";
        if (document.getNom().contains(".")) {
            extension = document.getNom().substring(document.getNom().lastIndexOf("."));
        }

        // Append extension to the new name if not already present
        if (!nouveauNom.endsWith(extension)) {
            nouveauNom += extension;
        }

        document.setNom(nouveauNom);
        return documentRepository.save(document);
    }


    @Transactional
    public void deleteDocument(Long idDocument) throws IOException{

    	ClientDocument document = documentRepository.findById(idDocument).orElse(null);

        Path filePath = Paths.get(document.getCheminFichier());
        Files.deleteIfExists(filePath);

        documentRepository.delete(document);
    }

    // A ne pas utiliser pour le moments sauf si le client a demander pour une suivit complet.
    public List<ClientDocument> getAllDocuments(){
        return documentRepository.findAll();
    }

    @Transactional
    public ClientDocument replaceDocument(Long idDocument, MultipartFile newFile, String newFileName) throws IOException {
        ClientDocument document = documentRepository.findById(idDocument)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        // Delete the old file
        Path oldFilePath = Paths.get(document.getCheminFichier());
        Files.deleteIfExists(oldFilePath);

        // If the new file name is not provided, use the original file name without extension
        String fileExtension = newFile.getOriginalFilename() != null
                ? newFile.getOriginalFilename().substring(newFile.getOriginalFilename().lastIndexOf("."))
                : ".pdf";  // Default to .pdf if no extension is found

        // Use the new file name provided by the user (if available)
        String uniqueFileName = (newFileName != null && !newFileName.isEmpty())
                ? newFileName + fileExtension
                : UUID.randomUUID().toString() + fileExtension; // Default to UUID if no new file name is provided

        String clientDir = uploadDir + "/" + document.getClientDocument().getIdClients();
        Path uploadPath = Paths.get(clientDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path newFilePath = uploadPath.resolve(uniqueFileName);
        Files.copy(newFile.getInputStream(), newFilePath);

        // Update file path and name in the entity
        document.setCheminFichier(clientDir + "/" + uniqueFileName);
        document.setNom(newFileName != null && !newFileName.isEmpty() ? newFileName : document.getNom()); // Set new name or keep the old one
        document.setDateAjout(LocalDateTime.now()); // Optional: Update date

        return documentRepository.save(document);
    }


}
