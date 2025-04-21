package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UniversiteCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUniversite;

    private EnumUniversite Univeriste;

    private String emailUniversite;
    private String passwordUniversite;


    @ManyToOne
    @JoinColumn(name = "credential_id")
    private Credential credential;


}
