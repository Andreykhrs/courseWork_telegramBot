package pro.sky.telegrambot.service;

import pro.sky.telegrambot.exception.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;


import java.util.Optional;

public interface NotificationService {

    void sheduleNotification(Notification notification, Long chatId);

    Optional<Notification> parseMessage(String notificationBotMessage) throws IncorrectMessageException;

    void sendNotificationMessage();
    void sendMessage(Long chatId, String messageText);
    void sendMessage(Notification notification);
}
