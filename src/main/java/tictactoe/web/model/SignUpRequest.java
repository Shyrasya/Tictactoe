package tictactoe.web.model;

public class SignUpRequest {

    /**
     * Новый логин в системе
     */
    private String login;

    /**
     * Новый пароль в системе
     */
    private String password;

    /**
     * Базовый конструктор класса SignUpRequest
     */
    public SignUpRequest() {
    }

    /**
     * Параметризированный конструктор класса SignUpRequest
     *
     * @param login    Логин
     * @param password Пароль
     */
    public SignUpRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
