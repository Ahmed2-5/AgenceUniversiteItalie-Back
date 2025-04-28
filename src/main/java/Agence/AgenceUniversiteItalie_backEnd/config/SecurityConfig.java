package Agence.AgenceUniversiteItalie_backEnd.config;


import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;
import Agence.AgenceUniversiteItalie_backEnd.security.CustomUserDetailsService;
import Agence.AgenceUniversiteItalie_backEnd.security.JwtAuthenticationFilter;
import Agence.AgenceUniversiteItalie_backEnd.security.JwtUtil;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(){return new CustomUserDetailsService();}

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, UtilisateurRepository utilisateurRepository){
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService, utilisateurRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Autorise Angular
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                config.setAllowCredentials(true);
                return config;
            }))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/utilisateurs/uploads/**").permitAll() // Allow public access to image resources
                    .requestMatchers(
                            "/api/utilisateurs/register",
                            "/api/utilisateurs/login",
                            "/api/utilisateurs/getRoleByLib/**",
                            "/api/utilisateurs/email",
                            "/api/utilisateurs/updateProfileByIdu/**",
                            "/oauth2/**",
                            "/api/password/reset",
                            "/api/password/forgot",
                            "/getNotifications/**",
                            "/getNotificationsByIsReadedOrNot/**",
                            "/createNotification",
                            "/getcreatedusers/**",
                            "/markAsRead/**",
                            "/getlistofReadedOrUnreadedNotifications/**",
                            "/MarkAllAsReaded/**",
                            "/api/utilisateurs/upload-profile-image/**",
                            "/api/utilisateurs/getUtilisateurByRole_LibelleRole/**",
                            "/api/utilisateurs/convertUsersToIds",
                            "/api/utilisateurs/countAdmins",
                            "/api/utilisateurs/countUsers",
                            "/api/Taches/getAllTachesOfAdmin",
                            "/api/Taches/GetAllTaches",
                            "/api/Taches/FindUsersFromIdtask/**",
                            "/api/Taches/gettacheById/**", 
                            "/api/Taches/status/**",
                            "/api/Taches/addTasktToUser/**",
                            "/api/Taches/getUserTakedByIdtask/**",
                            "/api/Taches/countAll",
                            "/api/Taches/countEnCours",
                            "/api/Taches/countDone",
                            "/api/Taches/countAllTasksAssignedByUser/**",
                            "/api/Taches/countTasksEnCoursByUser/**",
                            "/api/Taches/countTasksDoneByUser/**",
                            "/api/commentaire/addCommentaire",
                            "/api/commentaire/tache/**",
                            "/api/commentaire/**",
                            "/api/Clients/CreateClient",
                            "/api/Clients/UpdateClients/**",
                            "/api/Clients/deleteClient/**",
                            "/api/Clients/AllClients",
                            "/api/Clients/getclientById/**",
                            "/api/Clients/**",
                            "/api/Clients/assignedToTunisie/**",
                            "/api/Clients/assignedToItalie/**",
                            "/api/Clients/upload-profile-image/**",
                            "/api/Clients/uploads/**",
                            "/api/documents/Client/Documents/**",
                            "/api/documents/**",
                            "/api/documents/rename/**",
                            "/api/documents/replace/**",
                            "/api/paiements/ajouterPayment",
                            "/api/paiements/client/**",
                            "/api/paiements/**",
                            "/api/paiements/Tranches/**",
                            "/api/paiements/tranche/update-montant/**",
                            "/api/Credential/GetAllCredentials",
                            "/api/Credential/Clients/**",
                            "/api/Credential/createCredential/**",
                            "/api/Credential/deleteCredential/**",
                            "/api/Credential/**",
                            "/api/Universite-Credential/credential/**",
                            "/api/Universite-Credential/deleteUniversiteCredential/**",
                            "/api/Universite-Credential/getUniversiteCredentialById/**",
                            "/api/Universite-Credential/UpdateUniversiteCredential/**",
                            "/api/Universite-Credential/credential/**",
                            "/api/RDV/credential/**",
                            "/api/RDV/deleteRdvCredential/**",
                            "/api/RDV/getRDVById/**",
                            "/api/RDV/updateRDV/**",
                            "/api/RDV/RDVs/**"

                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    


}
