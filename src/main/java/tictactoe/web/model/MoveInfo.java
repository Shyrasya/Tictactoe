package tictactoe.web.model;

public class MoveInfo {

    /**
     * Сообщение о статусе игры в результате проверки поля или хода
     */
    String message;

    /**
     * Текущая игра
     */
    Game game;

    /**
     * Параметризированный конструктор класса MoveInfo
     *
     * @param message Сообщение о статусе игры в результате проверки поля или хода
     * @param game    Текущая игра
     */
    public MoveInfo(String message, Game game) {
        this.message = message;
        this.game = game;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
