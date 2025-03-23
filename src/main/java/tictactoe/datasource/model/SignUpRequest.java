package tictactoe.datasource.model;

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
