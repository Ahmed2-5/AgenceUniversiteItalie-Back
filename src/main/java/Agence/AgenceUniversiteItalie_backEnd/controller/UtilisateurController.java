package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Role;
import Agence.AgenceUniversiteItalie_backEnd.entity.StatusCompte;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.StatusCompteRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import Agence.AgenceUniversiteItalie_backEnd.security.JwtUtil;
import Agence.AgenceUniversiteItalie_backEnd.service.UtilisateurService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = {"http://localhost:4200",
        "http://universiteitalie.com",
        "https://universiteitalie.com",
        "http://www.universiteitalie.com",
        "https://www.universiteitalie.com"})
public class UtilisateurController {


    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StatusCompteRepository statusCompteRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Utilisateur utilisateur) {
        if (utilisateurService.utilisateurExiste(utilisateur.getAdresseMail())){
            return ResponseEntity.badRequest().body("Utilisateur already exist");
        }
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        Utilisateur savedUser = utilisateurService.createUser(utilisateur);
        return ResponseEntity.ok(savedUser);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurById(id);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getRoleByLib/{libelleRole}")
    public Optional<Role> getRoleByLibelleRole(@PathVariable EnumRole libelleRole) {
        Optional<Role> role = utilisateurService.getRoleByLibelleRole(libelleRole);
        return role;
    }
    
    @GetMapping("/getUtilisateurByRole_LibelleRole/{libelleRole}")
    public Optional<List<Utilisateur>> findAllByRole_LibelleRole(@PathVariable EnumRole libelleRole) {
        return utilisateurService.findAllByRole_LibelleRole(libelleRole);
    }

    @GetMapping("/alluser")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }


    //Deleting user

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUtilisateur(@PathVariable Long id) {
        boolean deleted = utilisateurService.deleteUser(id);
        if (deleted){
            return ResponseEntity.ok("Utilisateur deleted");
        }else {
            return ResponseEntity.notFound().build();
        }
    }


    // hedhi eli sarelha el update
    @PostMapping("/create-Admin")
    public ResponseEntity<?> createAdmin(@RequestBody Utilisateur admin , @RequestParam String superAdminEmail , @RequestParam EnumRole roleToAssign ) {

        Utilisateur newAdmin = utilisateurService.createAdmin(admin, superAdminEmail,roleToAssign);
        return ResponseEntity.ok(newAdmin);
    }

    @PostMapping("/createUserWithRole")
    public ResponseEntity<?> createUserWithRole(@RequestBody Map<String , String> requestBody , @RequestParam String superAdminEmail ) {
        String nom = requestBody.get("nom");
        String prenom = requestBody.get("prenom");
        String email = requestBody.get("email");
        String motDePasse = requestBody.get("motDePasse");
        String roleName = requestBody.get("role");

        try {
            Utilisateur newUser = utilisateurService.createUserWithRole(nom , prenom, email , motDePasse, roleName, superAdminEmail);
            return ResponseEntity.ok(newUser);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utilisateur utilisateur) {
        Optional<Utilisateur> userOpt = utilisateurService.getUtilisateurByEmail(utilisateur.getAdresseMail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Email ou mot de passe incorrect.");
        }

        Utilisateur user = userOpt.get();
        System.out.println("Mot de passe fourni : " + utilisateur.getMotDePasse());
        System.out.println("Mot de passe en base (haché) : " + user.getMotDePasse());

        if (!passwordEncoder.matches(utilisateur.getMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Email ou mot de passe incorrect.");
        }

     // ✅ Vérifie si le compte est activé
        if (user.getStatusCompte().getIdStatusCompte() == 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User inactive");
        }
        
        // ✅ Mise à jour de la date de dernière connexion
        user.setDateDerniereConnexion(LocalDateTime.now());

        String token = jwtUtil.generateToken(user);
        Map<String, Object> response = Map.of(
                "token", token,
                "email", user.getAdresseMail(),
                "role", user.getRole().getLibelleRole().toString() // Assurez-vous que Role a une méthode getLibelleRole()
        );

        return ResponseEntity.ok(response);    
       }



    @GetMapping("/activer-compte")
    public ResponseEntity<?> activerCompte(@RequestParam String email) {

        Optional<Utilisateur> utilisateurOpt = utilisateurService.getUtilisateurByEmail(email);

        if(utilisateurOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        if (utilisateur.getStatusCompte().getIdStatusCompte()==1){
            return ResponseEntity.ok("Votre compte est deja activé!");
        }

        StatusCompte statusCompteActif = statusCompteRepository.findById(1L)
                .orElseThrow(()-> new RuntimeException("StatusCompte ID=1 introuvable en base!")  );

        utilisateur.setStatusCompte(statusCompteActif);
        utilisateurService.updateUtilisateur(utilisateur);

        return ResponseEntity.ok("Votre Compte a ete activé avec succés !");
    }
    
    @GetMapping("/desactiver-compte")
    public ResponseEntity<?> desactiverCompte(@RequestParam String email) {

        Optional<Utilisateur> utilisateurOpt = utilisateurService.getUtilisateurByEmail(email);

        if(utilisateurOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        if (utilisateur.getStatusCompte().getIdStatusCompte()==2){
            return ResponseEntity.ok("Votre compte est deja desactivé!");
        }

        StatusCompte statusCompteActif = statusCompteRepository.findById(2L)
                .orElseThrow(()-> new RuntimeException("StatusCompte ID=2 introuvable en base!")  );

        utilisateur.setStatusCompte(statusCompteActif);
        utilisateurService.updateUtilisateur(utilisateur);

        return ResponseEntity.ok("Votre Compte a ete activé avec succés !");
    }
    
    @PutMapping(value = "/updateUtilisateurByIdu/{iduser}")
    public Utilisateur updateUtilisateurByIdu(@RequestBody Utilisateur user,@PathVariable long iduser) {
		
		return utilisateurService.updateUtilisateurByIdu(user,iduser);
	}
    
    @PutMapping(value = "/updateProfileByIdu/{iduser}")
    public Utilisateur updateProfileByIdu(@RequestBody Utilisateur user,@PathVariable long iduser) {
		
		return utilisateurService.updateUtilisateurByIdu(user,iduser);
	}
    
    @GetMapping("/email")
    public ResponseEntity<Utilisateur> getUtilisateurByEmail(@RequestParam String email) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurByEmail(email);
        
        if (utilisateur.isPresent()) {
            return ResponseEntity.ok(utilisateur.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/upload-profile-image/{id}")
    public ResponseEntity<?> uploadProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Ensure user exists
            Utilisateur utilisateur = utilisateurRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // Generate a unique filename
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Define the absolute path for the 'uploads' directory
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");

            // Create the directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Define the full file path
            Path filePath = uploadPath.resolve(filename);
            System.out.println("Uploading file to: " + filePath.toString());  // Debug log

            // Copy the file to the target path
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save the URL to the file in the database
            utilisateur.setProfileImageUrl(filename);  // Store only the filename
            utilisateurRepository.save(utilisateur);

            return ResponseEntity.ok("Image uploaded successfully. Access at: /uploads/" + filename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        // Define the path to the uploaded file
        Path file = Paths.get("uploads").resolve(filename);
        if (!Files.exists(file)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Determine the file type (content type) dynamically
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        MediaType mediaType = MediaType.IMAGE_JPEG; // Default to JPEG
        if ("png".equals(fileExtension)) {
            mediaType = MediaType.IMAGE_PNG;
        } else if ("gif".equals(fileExtension)) {
            mediaType = MediaType.IMAGE_GIF;
        }

        // Serve the file as a resource
        Resource resource = new FileSystemResource(file.toFile());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @PostMapping(value = "/convertUsersToIds")
	public ResponseEntity<List<Long>> convertUsersToIds(@RequestBody List<Utilisateur> users) {
	    List<Long> userIds = utilisateurService.convertUsersToIds(users);
	    return ResponseEntity.ok(userIds);
	}

    @GetMapping("/countAdmins")
    public long countAdmins() {
        return utilisateurService.getAdminCount();
    }
    
    @GetMapping("/countUsers")
    public long countUsers() {
        return utilisateurService.getUsersCount();
    }


}
