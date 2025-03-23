package tictactoe.domain.model;

import java.util.Set;
import java.util.UUID;

public class User {

    /**
     * Уникальный идентификатор пользователя
     */
    private UUID uuid;

    /**
     * Логин пользователя
     */
    private String login;

    /**
     * Хешированный пароль
     */
    private String password;

    /**
     * Список ролей у пользователя
     */
    private Set<Role> roles;

    /**
     * Токен обновления
     */
    private String refreshToken;

    /**
     * Базовый конструктор класса User
     */
    public User() {}

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
