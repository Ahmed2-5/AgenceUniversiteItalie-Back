package Agence.AgenceUniversiteItalie_backEnd.service;

import Agence.AgenceUniversiteItalie_backEnd.entity.EnumRole;
import Agence.AgenceUniversiteItalie_backEnd.entity.Role;
import Agence.AgenceUniversiteItalie_backEnd.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;


    @PostConstruct
    public void initRole(){
        List<EnumRole> roles = Arrays.asList(EnumRole.SUPER_ADMIN, EnumRole.ADMIN_TUNISIE, EnumRole.ADMIN_ITALIE, EnumRole.CLIENT);
        for (EnumRole enumRole: roles){
            Optional<Role> existingRole = roleRepository.findByLibelleRole(enumRole);
            if (existingRole.isEmpty()){
                roleRepository.save(new Role(enumRole));
            }
        }
    }
}
