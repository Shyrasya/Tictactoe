package tictactoe.domain.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Список ролей: 0 - обычный пользователь
 */
public enum Role implements GrantedAuthority {
    USER;

    /**
     * Выдает строковое значение роли
     * @return Строка-роль
     */
    @Override
    public String getAuthority() {
        return name();
    }
}
