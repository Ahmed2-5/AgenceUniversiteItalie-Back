package Agence.AgenceUniversiteItalie_backEnd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Agence.AgenceUniversiteItalie_backEnd.entity.Notification;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.repository.NotificationRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UtilisateurRepository;

@Service
public class NotificationService {

	@Autowired
    NotificationRepository notifrep;
	
	@Autowired
	UtilisateurRepository userrep;

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notifrep.findByUserId(userId);
    }
    
    public List<Notification> getNotificationsByIsReadedOrNot(Boolean isReaded) {
        return notifrep.findAllByReaded(isReaded);
    }

    public Notification createNotification(Notification notification) {
        return notifrep.save(notification);
    }
    
    public List<Utilisateur> getcreatedusers(Long iduser) {
        List<Utilisateur> listofcreatedusers = new ArrayList<>();
        List<Notification> listofnotifications = getNotificationsByUserId(iduser);
        for (Notification notification : listofnotifications) {
        	Utilisateur user = userrep.findById(notification.getCreatedby()).get();

            listofcreatedusers.add(user);
        }
        
        return listofcreatedusers;
	}
    
    public void markAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notifrep.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notifrep.save(notification);
        } else {
            throw new RuntimeException("Notification not found");
        }
    }
    
    
	public List<Boolean> getlistofReadedOrUnreadedNotifications(Long iduser) {
		List<Notification> listofnotifs = notifrep.findByUserId(iduser);
        List<Boolean> listofReadedOrUnreadedNotifications = new ArrayList<>();
        
        for (Notification notif : listofnotifs) {
        	listofReadedOrUnreadedNotifications.add(notif.isReadedorNot());
        }
        
        return listofReadedOrUnreadedNotifications;
	}
	
     	
	public void MarkAllAsReaded(Long userId) {
	        List<Notification> notifs = notifrep.findByUserId(userId);
	        for (Notification notif : notifs) {
	        	notif.setReaded(true);
	            notifrep.save(notif); 
	        }
	    } 
	
}
