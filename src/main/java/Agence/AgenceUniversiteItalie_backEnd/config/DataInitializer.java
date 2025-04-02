package Agence.AgenceUniversiteItalie_backEnd.config;

import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Role;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.RoleRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.StatusCompteRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private StatusCompteRepository statusCompteRepository;
    
    public DataInitializer(UtilisateurRepository utilisateurRepository,RoleRepository roleRepository, PasswordEncoder passwordEncoder , StatusCompteRepository statusCompteRepository){
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.statusCompteRepository = statusCompteRepository;

    }

    @Override
    public  void run(String... args){
        for(EnumRole role : EnumRole.values()){
            roleRepository.findByLibelleRole(role).orElseGet(() -> {
                Role newRole = new Role(role);
                return roleRepository.save(newRole);
            });
        }

        Optional<Role> superAdminRoleOpt = roleRepository.findByLibelleRole(EnumRole.SUPER_ADMIN);
        if(superAdminRoleOpt.isPresent()){
            Role superAdminRole = superAdminRoleOpt.get();
            Optional<Utilisateur> superAdminExist = utilisateurRepository.findByAdresseMail("bougachaahmed98@gmail.com");

            if(superAdminExist.isEmpty()){
                Utilisateur superAdmin = new Utilisateur();
                superAdmin.setNom("super");
                superAdmin.setPrenom("Admin");
                superAdmin.setAdresseMail("bougachaahmed98@gmail.com");
                superAdmin.setMotDePasse(passwordEncoder.encode("admin123"));
                superAdmin.setDateCreation(LocalDateTime.now());
                superAdmin.setRole(superAdminRole);

                utilisateurRepository.save(superAdmin);
                System.out.println("Super admin added");
            }
        }else {
            System.out.println("le super n'existe pas , impossible de  créer un admin");
        }
    }


}
