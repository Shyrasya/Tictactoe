package tictactoe.security.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class JwtAuthentication implements Authentication {

    /**
     * Уникальный идентификатор пользователя.
     */
    private final UUID userUuid;

    /**
     * Роли пользователя, представленные в виде множества {@link GrantedAuthority}.
     */
    private final Set<GrantedAuthority> roles;

    /**
     * Флаг, указывающий на аутентификацию пользователя. По умолчанию true.
     */
    private boolean isAuthenticated;

    /**
     * Конструктор для создания объекта {@link JwtAuthentication}.
     *
     * @param userUuid Уникальный идентификатор пользователя.
     * @param roles Набор ролей пользователя.
     */
    public JwtAuthentication(UUID userUuid, Set<GrantedAuthority> roles) {
        this.userUuid = userUuid;
        this.roles = roles;
        this.isAuthenticated = true;
    }

    /**
     * Возвращает список ролей, назначенных пользователю.
     *
     * @return Коллекция объектов {@link GrantedAuthority}, представляющих роли пользователя.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    /**
     * Возвращает уникальный идентификатор пользователя.
     *
     * @return Уникальный идентификатор пользователя.
     */
    @Override
    public Object getPrincipal() {
        return userUuid;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    /**
     * Возвращает строковое представление имени пользователя (UUID в виде строки).
     *
     * @return UUID пользователя в виде строки.
     */
    @Override
    public String getName() {
        return userUuid.toString();
    }

    /**
     * Возвращает учётные данные пользователя.
     *
     * @return null, так как учётные данные не сохраняются в данном объекте.
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Возвращает дополнительные детали аутентификации.
     *
     * @return null, так как дополнительные детали не сохраняются в данном объекте.
     */
    @Override
    public Object getDetails() {
        return null;
    }
}
