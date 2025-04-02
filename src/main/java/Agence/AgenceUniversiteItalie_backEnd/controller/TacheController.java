package Agence.AgenceUniversiteItalie_backEnd.controller;


import Agence.AgenceUniversiteItalie_backEnd.entity.EnumStatutTache;
import Agence.AgenceUniversiteItalie_backEnd.entity.Tache;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.TacheRepository;
import Agence.AgenceUniversiteItalie_backEnd.service.TacheServie;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/Taches")
public class TacheController {

    private final TacheServie tacheService;
    private TacheRepository tacheRepository;

    public TacheController(TacheServie tacheService) {
        this.tacheService = tacheService;
    }

    /**************************************Test*******************************************************/
    // ✅ SuperAdmin creates a task ( Do not use)
    @PostMapping("/create")
    public ResponseEntity<?> createTache(@RequestParam String titre,
                                         @RequestParam String description,
                                         @RequestParam String superAdminEmail,
                                         @RequestParam List<Long> adminIds) {
        Tache tache = tacheService.createTacheTest(titre, description, superAdminEmail, adminIds);
        return ResponseEntity.ok(tache);
    }

    @GetMapping("/GetAllTaches")
    public ResponseEntity<List<Tache>> getAllTaches() {
        return ResponseEntity.ok(tacheService.getAllTaches());
    }

    // do Not use
    @GetMapping("/GetTacheById")
    public ResponseEntity<Tache> getTacheButId(@RequestParam long id) {
        Optional<Tache> tache = tacheService.getTacheById(id);
        return tache.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**************************************Work*******************************************************/

    /**
     *
     * @param tache
     * @param superAdminEmail
     * @param adminIds
     * @return Create Tache with SuperAdmin
     */
    @PostMapping("/CreateTache")
    public ResponseEntity<?> createTache(@RequestBody Tache tache,
                                         @RequestParam String superAdminEmail,
                                         @RequestParam List<Long> adminIds){
        try {
            Tache createdTache = tacheService.createTache(tache,superAdminEmail,adminIds);
            return ResponseEntity.ok(createdTache);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // get tache by Id ( to use )

    @GetMapping("/gettacheById/{id}")
    public ResponseEntity<?> getTacheById(@PathVariable Long id){
        try {
            Tache tache = tacheService.getTacheById(id);
            return ResponseEntity.ok(tache);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get all Taches created by the SuperAdmin ( to use in the superAdmin Dashborad)
    @GetMapping("/getAllTachesCreatedBySuperAdmin")
    public ResponseEntity<?> getTachesByCreator(@RequestParam String superAdminEmail){
        try {
            List<Tache> taches = tacheService.getTachesByCreator(superAdminEmail);
            return ResponseEntity.ok(taches);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get all Tasks Assigned to an admin ( to be use in the dashboard of the admin)
    @GetMapping("/getAllTachesOfAdmin")
    public ResponseEntity<?> getAllTachesOfAdmin(@RequestParam String adminEmail){
        try {
            List<Tache> taches = tacheService.getTachesAssignedToAdmin(adminEmail);
            return ResponseEntity.ok(taches);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update Task Status by Admin
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateTacheStatus(@PathVariable Long id,
                                               @RequestBody Map<String,String> statusRequest,
                                               @RequestParam String adminEmail){
        try {
            EnumStatutTache newStatus = EnumStatutTache.valueOf(statusRequest.get("status"));
            Tache updatedTache = tacheService.updateTacheStatus(id,newStatus,adminEmail);
            return ResponseEntity.ok(updatedTache);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value = "/addTasktToUser/{idtask}/{iduser}")
	public Tache addTasktToUser(@PathVariable Long idtask,@PathVariable Long iduser) {
		return tacheService.addTasktToUser(idtask,iduser);
	}
   
    @GetMapping(value = "/getUserTakedByIdtask/{idtask}")
	public Utilisateur getUserTakenByIdtask(@PathVariable Long idtask) {
		return tacheService.getUserTakenByIdtask(idtask);
	}
    
    // Delete a Taches
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTache(@PathVariable Long id,
                                                           @RequestParam String superAdminEmail){
        try {
            tacheService.deleteTache(id, superAdminEmail);
            
            // Returning a JSON response
            Map<String, String> response = new HashMap<>();
            response.put("message", "Task deleted Successfully");
            return ResponseEntity.ok(response);  // Returning JSON
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping(value = "/updateTask/{idtask}")
    public Tache updateTask(@RequestBody Tache task,@PathVariable long idtask) {
		
		return tacheService.updateTache(task,idtask);
	}


    @GetMapping(value = "/FindUsersFromIdtask/{idtask}")
	public List<Utilisateur> FindUsersFromIdtask(@PathVariable Long idtask){
	    return tacheService.FindUsersFromIdtask(idtask);
	}

    @GetMapping("/countAll")
    public long countAllTasks() {
        return tacheService.getTotalTasks();
    }

    @GetMapping("/countEnCours")
    public long countTasksEnCours() {
        return tacheService.getTasksEnCours();
    }

    @GetMapping("/countDone")
    public long countTasksDone() {
        return tacheService.getTasksDone();
    }

}