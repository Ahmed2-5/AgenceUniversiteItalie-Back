package Agence.AgenceUniversiteItalie_backEnd.service;

import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Role;
import Agence.AgenceUniversiteItalie_backEnd.entity.StatusCompte;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.RoleRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.StatusCompteRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {



    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StatusCompteRepository statusCompteRepository;

    @Autowired
    private EmailService emailService;


    //initialise le compte Super Admin

    @PostConstruct
    public void createSuperAdmin(){
        if(utilisateurRepository.findByAdresseMail("admin@universiteitalie.com").isPresent()){
            System.out.println("Utilisateur already exist");
            return;
        }
        // verification si le super Admin existe ou non
        Role superAdminRole = roleRepository.findByLibelleRole(EnumRole.SUPER_ADMIN)
                .orElseThrow(()-> new RuntimeException("Le Role super n'existe pas "));


        String encodedPassword = passwordEncoder.encode("admin123");

        Utilisateur superAdmin = new Utilisateur("Admin","Super","admin@universiteitalie.com",encodedPassword,superAdminRole);

        utilisateurRepository.save(superAdmin);
        System.out.println("Admin creer avec success");
    }

    // cree un utilisateur

    public Utilisateur createUser(Utilisateur utilisateur){
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le mot de pass ne peut pas etre vide");
        }

        if (utilisateurExiste(utilisateur.getAdresseMail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"ce mail est deja utilisé");
        }

        Role role = roleRepository.findByLibelleRole(utilisateur.getRole().getLibelleRole())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"le rôle specifie n'existe pas "));
        utilisateur.setRole(role);

        StatusCompte statusCompte;
        if (role.getLibelleRole() == EnumRole.CLIENT){
            statusCompte = statusCompteRepository.findById(2L)
                    .orElseThrow(()-> new RuntimeException("Status Compte (Desactiver) introuvable"));
        }else {
            statusCompte = statusCompteRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Status Compte Actif introuvable"));
        }
        utilisateur.setStatusCompte(statusCompte);

        //Encode MotDePasse
        if (!utilisateur.getMotDePasse().startsWith("$2a$") && !utilisateur.getMotDePasse().startsWith("$2b$")){
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        }


        //save utilisateur

        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        System.out.println("Utilisateur creer avec status :"+ savedUser.getStatusCompte().getIdStatusCompte());

        if (role.getLibelleRole() == EnumRole.CLIENT){
            emailService.envoyerEmailActivation(savedUser);
        }
        return savedUser;
    }

    public boolean utilisateurExiste(String email) {
        return utilisateurRepository.findByAdresseMail(email).isPresent();
    }


    public Optional<Utilisateur> getUtilisateurById(Long id){return utilisateurRepository.findById(id);}

    public List<Utilisateur> getAllUtilisateurs(){return utilisateurRepository.findAll();}

    public boolean deleteUser(Long id){
        if (!utilisateurRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"l'utilisateur est introvable");
        }

        utilisateurRepository.deleteById(id);
        return true;
    }

    public GrantedAuthority getGrantedAuthority(Role role){
        return new SimpleGrantedAuthority("ROLE_" + role.getLibelleRole().name());
    }

// hethy li nestamell feha !!!!!!!!!!!!!!!!!!!!!!!!
    public Utilisateur createAdmin(Utilisateur admin , String superAdminEmail){
        Utilisateur superAdmin = utilisateurRepository.findByAdresseMail(superAdminEmail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you must be a superAdmin"));

        if (!getGrantedAuthority(superAdmin.getRole()).getAuthority().equals("ROLE_SUPER_ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"only the Super Admin  user can create admin ");
        }

        if (utilisateurExiste(admin.getAdresseMail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"cet email est deja utilise");
        }

        Role adminRole = roleRepository.findByLibelleRole(EnumRole.ADMIN)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"le role admin n'existe pas"));

        StatusCompte statusCompte;
      
        statusCompte = statusCompteRepository.findById(1L).orElseThrow(() -> new RuntimeException("Status Compte Actif introuvable"));
        admin.setStatusCompte(statusCompte);
        admin.setRole(adminRole);
        emailService.envoyerEmailAjoutNouveauAdmin(admin);
        admin.setMotDePasse(passwordEncoder.encode(admin.getMotDePasse()));

        return utilisateurRepository.save(admin);
    }


    public Utilisateur createUserWithRole(String nom , String prenom , String email , String motDepasse , String roleName , String superAdminEmail){
        System.out.println("the superAdmin fait le demande : " +superAdminEmail);

        Utilisateur superAdmin = utilisateurRepository.findByAdresseMail(superAdminEmail)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"only the Super can create utilisateur"));
        System.out.println("Role :" + superAdmin.getRole().getLibelleRole());


        if (!superAdmin.getRole().getLibelleRole().equals(EnumRole.SUPER_ADMIN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"only the super Admin ");
        }

        if (utilisateurRepository.findByAdresseMail(email).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"this email est deja utilisé");
        }

        EnumRole enumRole;
        try {
            enumRole = EnumRole.valueOf(roleName.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"le role invalide");
        }

        Role role = roleRepository.findByLibelleRole(enumRole)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,   "Le role " +roleName+ "n'existe pas"));

        //creation de l'utilisateur
        Utilisateur newUser = new Utilisateur();
        newUser.setNom(nom);
        newUser.setPrenom(prenom);
        newUser.setAdresseMail(email);
        newUser.setMotDePasse(passwordEncoder.encode(motDepasse));
        newUser.setRole(role);
        newUser.setDateCreation(LocalDateTime.now());

        return utilisateurRepository.save(newUser);

    }

    public Optional<Utilisateur> getUtilisateurByEmail(String email){
        Optional<Utilisateur> user = utilisateurRepository.findByAdresseMail(email);
        if (user.isPresent()){
            System.out.println("Utilisateur trouver" + user.get().getAdresseMail());
        }else {
            System.out.println("Utilisateur introvable avec cette adresse:"+email);
        }
        return user;
    }


    public void updateUtilisateur(Utilisateur utilisateur){
        utilisateurRepository.save(utilisateur);
        System.out.println("Date de derniere Connexion mise a jour :" +utilisateur.getDateDerniereConnexion());
    }
    
	public Utilisateur updateUtilisateurByIdu(Utilisateur user, long iduser) {
		Utilisateur usr = utilisateurRepository.findById(iduser).get();
		usr.setAdresseMail(user.getAdresseMail());
		usr.setNom(user.getNom());
		usr.setPrenom(user.getPrenom());
		usr.setTelephone(user.getTelephone());
		usr.setRole(user.getRole());
		return utilisateurRepository.save(usr);
	}
	
    public Optional<Role> getRoleByLibelleRole(EnumRole libelleRole){return roleRepository.findByLibelleRole(libelleRole);}

    public Optional<List<Utilisateur>> findAllByRole_LibelleRole(EnumRole libelleRole){
    	return utilisateurRepository.findAllByRole_LibelleRole(libelleRole);}

    public List<Long> convertUsersToIds(List<Utilisateur> users) {
        return users.stream()
                    .map(Utilisateur::getIdUtilisateur)
                    .collect(Collectors.toList());
    }
    
    public long getAdminCount() {
        return utilisateurRepository.countAdmins();
    }
    
    public long getUsersCount() {
        return utilisateurRepository.countAllUsers();
    }
    
    

}
