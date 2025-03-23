package tictactoe.web.model;

public class ErrorMessage {

    /**
     * Сообщение, выводимое при ошибке
     */
    private String message;

    /**
     * Конструктор класса ErrorMessage
     *
     * @param message Сообщение, выводимое при ошибке
     */
    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
