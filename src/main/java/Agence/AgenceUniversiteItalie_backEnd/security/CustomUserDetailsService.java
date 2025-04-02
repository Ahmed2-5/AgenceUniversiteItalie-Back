package Agence.AgenceUniversiteItalie_backEnd.security;

import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByAdresseMail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé" + email));

        return new User(
                utilisateur.getAdresseMail(),
                utilisateur.getMotDePasse(),
                Collections.singleton(()->"Role_"+utilisateur.getRole().getLibelleRole().name()) // Assigne le Role

        );
    }

}
