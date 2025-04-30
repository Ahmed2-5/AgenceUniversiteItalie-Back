package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.LogAction;
import Agence.AgenceUniversiteItalie_backEnd.service.LogActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/logs")
public class LogActionController {

    @Autowired
    private LogActionService logActionService;


    @GetMapping("/getAllLogs")
    public ResponseEntity<List<LogAction>> getAllLogs(){
        return ResponseEntity.ok(logActionService.findAllLogs());
    }

    @GetMapping("/entity/{type}/{id}")
    public ResponseEntity<List<LogAction>> getLogByEntity(@PathVariable String type,
                                                          @PathVariable Long id){
        return ResponseEntity.ok(logActionService.findLogsByEntite(type,id));
    }

    @GetMapping("/admin/{idAdmin}")
    public ResponseEntity<List<LogAction>> getLogsByAdmin(@PathVariable Long idAdmin) {
        return ResponseEntity.ok(logActionService.findLogsByAdmin(idAdmin));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LogAction>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(logActionService.findLogsByDateRange(debut, fin));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LogAction>> searchLogsByTitle(@RequestParam String titre) {
        return ResponseEntity.ok(logActionService.findLogsByTitle(titre));
    }


}
