package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.IncorrectMessageException;
import pro.sky.telegrambot.service.NotificationService;

import javax.management.Notification;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static final String REGEX_MSG = "([0-9\\.\\:\\s]{16})(\\s)([\W+]+)";

    private final NotificationRepository repository;
    private TelegramBot telegramBot;

    public NotificationServiceImpl(NotificationRepository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }

    @Override
    public void scheduleNotification(Notification notification, Long chatId) {
        notification.setChatId(chatId);
        Notification savedNotification = repository.save(notification);
        logger.info("Notification " + savedNotification + " sheduled ");
    }

    @Override
    public Optional<Notification> parseMessage(String message) throws IncorrectMessageException {
        Notification notification = null;

        Pattern pattern = Pattern.compile(REGEX_MSG);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String messageToSave = matcher.group(3);
            LocalDateTime notificationDateTime =
                    LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            if (notificationDateTime.isAfter(LocalDateTime.now())) {
                notification = new Notification(messageToSave, notificationDateTime);
                logger.info("Saving {} to db", notification);
                repository.save(notification);
            } else {
                logger.error("Date is incorrect");
                throw  new IncorrectMessageExcepion("Incorrrect date");
            }
        }
        return Optional.ofNullable(notification);
    }

    @Override
    public void sendNotificationMessage() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<Notification> notifications = repository.findByNotificationDate(currentTime);
        notifications.forEach(task -> {
            sendMessage(task);
            task.setAsSert();
            logger.info("Notification was sent {}", task);
        });
        repository.saveAll(notifications);
        logger.info("Notifications were saved");
    }

    public void sendMessage(Long chatId, String messageText) {
        SendMessage sendMessage = new sendMessage(chatId, messageText);
        telegramBot.execute(sendMessage);
        logger.info("Message was sent: {}", messageText);
    }

    public void sendMessage(Notification notification) {
        sendMessage(notification.getChatId(), notification.getMessage());
    }

}
