package Agence.AgenceUniversiteItalie_backEnd.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRole;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private EnumRole libelleRole ;

    public Role (EnumRole libelleRole) {
        this.libelleRole = libelleRole;
    }
}
