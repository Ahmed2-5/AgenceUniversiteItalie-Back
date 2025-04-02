package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.PasswordResetToken;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.PasswordResetTokenRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    public void envoyerEmailActivation(Utilisateur utilisateur) {
        try {
            if(utilisateur.getAdresseMail() == null || utilisateur.getAdresseMail().isEmpty()) {
                System.out.println("Erreur : adresse mail non valide");
                return;
            }

            String sujet = "Activation de votre compte Universite Italie";
            String lienActivation = "http://localhost:8082/api/utilisateurs/activer-compte?email=" + utilisateur.getAdresseMail();

            String message = "Bonjour !" + utilisateur.getNom() + ",\n\n"
                    + "Merci pour votre Inscription sur Universite Italie! \n"
                    + "Veuillez cliquer sur le lien pour activer votre Compte: \n\n"
                    +lienActivation + "\n\n"
                    +"Cordialement, \n L'equipe Université Italie.";

            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom("Noreply@universiteitalie.com");
            email.setTo(utilisateur.getAdresseMail());
            email.setSubject(sujet);
            email.setText(message);

            mailSender.send(email);
            System.out.println("email d'activation a été envoyer a : " + utilisateur.getAdresseMail());
        }catch (Exception e){
            System.err.println("erreur lors de l'envoi d'un email : " + e.getMessage());
        }
    }

    public void envoyerEmailAjoutNouveauAdmin(Utilisateur utilisateur) {
        try {
            if (utilisateur.getAdresseMail() == null || utilisateur.getAdresseMail().isEmpty()) {
                System.out.println("Erreur : adresse mail non valide");
                return;
            }

            String sujet = "Bienvenue en tant que nouvel Administrateur";
            
            // Vérifier si l'utilisateur est déjà sauvegardé
            if (utilisateur.getIdUtilisateur() == null) {
                utilisateur = utilisateurRepository.save(utilisateur);
            }

            // Lien pour réinitialiser le mot de passe
            String token = UUID.randomUUID().toString();

            tokenRepository.findByUtilisateur(utilisateur).ifPresent(tokenRepository::delete);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUtilisateur(utilisateur);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

            tokenRepository.save(resetToken);

            String resetLink = "http://localhost:4200/#/reset-password?token=" + token;

            // Message personnalisé incluant le mot de passe généré
            String message = "Bonjour " + utilisateur.getNom() + " " + utilisateur.getPrenom() + ",\n\n"
                    + "Félicitations, vous êtes maintenant un administrateur sur Agence Université Italie ! \n\n"
                    + "Voici vos informations d'accès : \n"
                    + "Email : " + utilisateur.getAdresseMail() + "\n"
                    + "Mot de passe : " + utilisateur.getMotDePasse() + "\n\n"
                    + "Si vous souhaitez réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant : \n"
                    + resetLink + "\n\n"
                    + "Cordialement, \nL'équipe Université Italie.";

            // Envoi de l'email
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom("Noreply@universiteitalie.com");
            email.setTo(utilisateur.getAdresseMail());
            email.setSubject(sujet);
            email.setText(message);

            mailSender.send(email);
            System.out.println("Email d'activation envoyé à : " + utilisateur.getAdresseMail());

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }


    
    public void sendSimpleEmail(String client , String subject , String message){
        try {
            if (client == null || client.isEmpty()){
                System.out.println("l'adresse est vide");
                return;
            }

            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom("Noreply@universiteitalie.com");
            email.setTo(client);
            email.setSubject(subject);
            email.setText(message);

            mailSender.send(email);
            System.out.println("email envoyer a :" +client);
        }catch (Exception e){
            System.err.println("Errueur lors de l'envoi de l'email:" +e.getMessage());
        }
    }
}
