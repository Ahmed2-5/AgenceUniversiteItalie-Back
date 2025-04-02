package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.PasswordResetToken;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.PasswordResetTokenRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public void sendResetPasswordEmail(String email){

        Utilisateur utilisateur = utilisateurRepository.findByAdresseMail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "utilisateur introvable !"));

        String token = UUID.randomUUID().toString();

        tokenRepository.findByUtilisateur(utilisateur).ifPresent(tokenRepository::delete);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUtilisateur(utilisateur);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/#/reset-password?token=" + token;
        emailService.sendSimpleEmail(email, "Réintialisation du mot de passe",
                "cliquer sur le lien pour le reset : " + resetLink);
        System.out.println("Email de reset a été envoyer a : " +email);
    }

    public void resetPassword(String token , String newPassword, String confirmPassword){
        if (!newPassword.equals(confirmPassword)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"les mots de passe ne sont pas kifkif ! ");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Token invalide ou expiré"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())){
            tokenRepository.delete(resetToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token expiré c'est confirmer mon ami");
        }


        Utilisateur utilisateur = resetToken.getUtilisateur();
        utilisateur.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(utilisateur);


        tokenRepository.delete(resetToken);
        System.out.println("Mot de passes mis a jour pour : " +utilisateur.getAdresseMail());

    }
}
