package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import pro.sky.telegrambot.exception.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repositiry.NotificationsRepository;
import pro.sky.telegrambot.service.impl.NotificationServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationServiceImplTest {

    @Autowired
    NotificationsRepository notificationsRepository;

    @SpyBean
    NotificationServiceImpl out;

    @SpyBean
    TelegramBot telegramBot;

    @Captor
    ArgumentCaptor<SendMessage> messageCaptor;

    @LocalServerPort
    private int port;

    @Test
    void should_parse_message_successfully() throws IncorrectMessageException {
        //setup
        Notification expected = new Notification("тест",
                LocalDateTime.parse("11.06.2023 22:22", DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm")));
        //run
        out.parseMessage("10.06.2023 22:22 тест");
        //assert
        assertTrue(
                notificationsRepository
                findAll().stream().anyMatch(s -> s.getMessage().equals(expected.getMessage()));
    }

    @Test
    void should_invoke_send_message() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Notification notification = new Notification("тест", currentTime);
        notification.setChatId(1L);
        notificationsRepository.save(notification);

        out.sendNotificationMessage();

        verify(out, times(1)).sendMessage(any());
        verify(telegramBot).execute(messageCaptor.capture());
        SendMessage value = messageCaptor.getValue();
        assertEquals("тест", value.getParameters().get("text"));
        assertEquals(1L, value.getParameters().get("chat_id"));
    }

    @Test
    void should_send_expected_message() {
        //setup
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Notification notification = new Notification("тест", currentTime);
        notification.setChatId(1L);

        out.sendMessage(notification);

        verify(telegramBot).execute(messageCaptor.capture());
        sendMessage value = messageCaptor.getValue();
        assertEquals("тест", value.getParametrs().get("text"));
        assertEquals(1L, value.getParametrs().get("chat_id"));
    }
}
