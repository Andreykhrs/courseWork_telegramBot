package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Long chatId;

    private String message;

    private LocalDateTime notificationDate;

    private LocalDateTime notificationSent;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.SCHEDULED;

    public Notification() {
    }

    public Notification(String notificationMessage, LocalDateTime notificationDate) {
        this.message = notificationMessage;
        this.notificationDate = notificationDate;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return getMessage();
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public LocalDateTime getNotificationSent() {
        return notificationSent;
    }

    public NotificationStatus getStatus() {
        return getStatus();
    }

    public void setAsSent() {
        this.status = NotificationStatus.SENT;
        this.notificationSent = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(Id, that.Id) && Objects.equals(chatId, that.chatId) && Objects.equals(message, that.message) && Objects.equals(notificationDate, that.notificationDate) && Objects.equals(notificationSent, that.notificationSent) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, chatId, message, notificationDate, notificationSent, status);
    }
}
