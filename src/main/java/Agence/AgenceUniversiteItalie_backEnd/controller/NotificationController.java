package Agence.AgenceUniversiteItalie_backEnd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import Agence.AgenceUniversiteItalie_backEnd.entity.Notification;
import Agence.AgenceUniversiteItalie_backEnd.entity.Utilisateur;
import Agence.AgenceUniversiteItalie_backEnd.service.NotificationService;

@CrossOrigin(origins={"http://localhost:4200",
        "http://universiteitalie.com",
        "https://universiteitalie.com",
        "http://www.universiteitalie.com",
        "https://www.universiteitalie.com"})
@RestController
@RequestMapping("/api")
public class NotificationController {

	@Autowired
    private NotificationService notificationService;

    @GetMapping(value = "/getNotifications/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
        return notificationService.getNotificationsByUserId(userId);
    }
    
    @GetMapping(value = "/getNotificationsByIsReadedOrNot/{isReaded}")
    public List<Notification> getNotificationsByIsReadedOrNot(@PathVariable Boolean isReaded) {
        return notificationService.getNotificationsByIsReadedOrNot(isReaded);
    }
  
    @PostMapping(value = "/createNotification")
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }
    
    @GetMapping(value = "/getcreatedusers/{userId}")
    public List<Utilisateur> getcreatedusers(@PathVariable Long userId) {
        return notificationService.getcreatedusers(userId);
    }
    
    @PutMapping("/markAsRead/{notificationId}")
    public void markAsRead(@PathVariable Long notificationId) {
        System.out.println("Mark as read endpoint hit with ID: " + notificationId);
        notificationService.markAsRead(notificationId);
    }
    
    @GetMapping(value = "/getlistofReadedOrUnreadedNotifications/{userId}")
    public List<Boolean> getlistofReadedOrUnreadedNotifications(@PathVariable Long userId) {
        return notificationService.getlistofReadedOrUnreadedNotifications(userId);
    }
    
    @PutMapping("/MarkAllAsReaded/{userId}")
    public void MarkAllAsReaded(@PathVariable Long userId) {
        System.out.println("Mark All as read done!");
        notificationService.MarkAllAsReaded(userId);
    }
}
