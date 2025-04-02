package Agence.AgenceUniversiteItalie_backEnd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Agence.AgenceUniversiteItalie_backEnd.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    
    List<Notification> findAllByReaded(Boolean isReaded);
}
