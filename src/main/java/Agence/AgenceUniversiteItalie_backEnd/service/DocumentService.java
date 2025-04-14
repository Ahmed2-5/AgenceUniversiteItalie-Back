package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.Clients;
import Agence.AgenceUniversiteItalie_backEnd.entity.Document;
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


    public Document getDocumentById(Long idDocument){
        return documentRepository.findById(idDocument).
                orElseThrow(()->new EntityNotFoundException("Documents not found whith this id"+idDocument));
    }

    public List<Document> getDocumentByClient(Long idClient){
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
    public Document uploadDocument(MultipartFile file , String nom , Long idClient, Long idUtilisateur)throws IOException{
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

        Document document = new Document();
        document.setNom(nom);
        document.setCheminFichier(clientDir + "/" +uniqueFileName );
        document.setClientDocument(clients);
        document.setAjouterPar(utilisateur);
        document.setDateAjout(LocalDateTime.now());

        return documentRepository.save(document);

    }


    @Transactional
    public Document updateDocument(Long idDoc, String nouveauNom){
        Document document= documentRepository.findById(idDoc).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"le document n'est pas trouver"));

        document.setNom(nouveauNom);
        return documentRepository.save(document);
    }













}
