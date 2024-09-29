package pro.sky.telegrambot.exception;

public class IncorrectMessageException extends Exception{
    public IncorrectMessageException(String name) {
        super("Incorrect message: " + name);
    }
}
