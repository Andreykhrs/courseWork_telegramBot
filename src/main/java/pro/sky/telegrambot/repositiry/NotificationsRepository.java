package pro.sky.telegrambot.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.Notification;

import java.time.LocalDateTime;
import java.util.List;


public interface NotificationsRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByNotificationDate(LocalDateTime dateTime);

}
