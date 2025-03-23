package tictactoe.web.exception;

public class GameNotFoundException extends RuntimeException {
    /**
     * Конструктор класса GameNotFoundException
     *
     * @param message Сообщение о не нахождении игры
     */
    public GameNotFoundException(String message) {
        super(message);
    }
}
