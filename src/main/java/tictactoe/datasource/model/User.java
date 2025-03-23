package tictactoe.datasource.model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @Column(columnDefinition = "UUID", nullable = false)
    private UUID uuid;

    /**
     * Логин пользователя
     */
    @Column(unique = true, nullable = false)
    private String login;

    /**
     * Хешированный пароль
     */
    @Column(nullable = false)
    private String password;

    /**
     * Список ролей у пользователя
     */
    @Convert(converter = RoleConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private Set<Role> roles;

    /**
     * Токен обновления
     */
    @Column
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
