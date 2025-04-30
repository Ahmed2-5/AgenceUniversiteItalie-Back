package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.LogAction;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.LogActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogActionService {

    @Autowired
    private LogActionRepository logActionRepository;





    public LogAction ajouterLog(String titre , String contenu, String typeEntite, Long idEntite, Utilisateur utilisateur){
        LogAction log = new LogAction(titre, contenu, typeEntite, idEntite, utilisateur);
        return logActionRepository.save(log);
    }

    public List<LogAction> findLogsByAdmin(Long idAdmin) {
        return logActionRepository.findByAdmin_IdUtilisateur(idAdmin);
    }

    public List<LogAction> findAllLogs(){
        return logActionRepository.findAll();
    }

    public List<LogAction> findLogsByEntite(String typeEntite, Long idEntite){
        return logActionRepository.findByTypeEntiteAndIdEntite(typeEntite, idEntite);
    }

    public List<LogAction> findLogsByDateRange(LocalDateTime debut, LocalDateTime fin){
        return logActionRepository.findByDateActionBetween(debut, fin);
    }

    public List<LogAction> findLogsByTitle(String titre){
        return logActionRepository.findByTitreContaining(titre);
    }



}
