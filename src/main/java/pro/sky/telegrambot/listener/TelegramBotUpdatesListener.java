package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.management.Notification;
import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotificationService notificationService;
    private final TelegramBot telegramBot;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationService notificationService) {
        this.notificationService = notificationService;
        this.telegramBot = telegramBot;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotificationMessage() {
        notificationService.sendNotificationMessage();
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();

            if (message.text().startsWith(START_CMD)) {
                logger.info(START_CMD + " " + LocalDateTime.now());
                notificationService.sendMessage(getChatId(message), WELCOM + message.from().firstName() + " ");
                notificationService.sendMessage(message), HELP_MSG);
            } else {
                try {
                    notificationService
                            .parseMessage(message.text())
                            .ifPresentOrElse(
                                    task -> sheduledNotification(getChatId(message), task),
                                    () -> notificationService.sendMessage(getChatId(message), INVALID_MESSAGE)
                            );
                } catch (IncorrectMessageException e) {
                    notificationService.sendMessage(getChatId(message), "Сообщение не соответствует требуемому формату");
                }
            }
            // Process your updates here
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void scheduledNotification(Long chatId, Notification notification) {
        notificationService.sheduleNotification(notification, chatId);
        notificationService.sendMessage(chatId, "The task: " + notification.getMessage() + " is created");
    }

    private Long getChatId(Message message) {
        return message.chat().id();
    }
}
