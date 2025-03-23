package tictactoe.web.model;

public class JwtRequest {

    /**
     * Введенный логин пользователя при проверке данных при входе
     */
    private String login;

    /**
     * Введенный пароль пользователя при проверке данных при входе
     */
    private String password;

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
