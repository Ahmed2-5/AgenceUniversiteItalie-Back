package Agence.AgenceUniversiteItalie_backEnd.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Notification {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnotif;
    
    private String notifLib;
    private Long userId;
    private Long createdby;
    private String message;
    private boolean readed = false;
    
    private LocalDateTime notificationDate;

    public boolean isReadedorNot(){
    	return this.readed;
    }
}
