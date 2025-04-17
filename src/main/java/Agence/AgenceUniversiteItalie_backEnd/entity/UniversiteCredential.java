package Agence.AgenceUniversiteItalie_backEnd.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    // Many to one with Credential


}
