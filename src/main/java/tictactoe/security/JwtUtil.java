package tictactoe.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tictactoe.security.model.JwtAuthentication;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtUtil {

    /**
     * Создаёт объект аутентификации {@link JwtAuthentication} на основе переданных {@link Claims}.
     *
     * @param claims Объект {@link Claims}, содержащий данные, извлечённые из JWT токена.
     *               Включает UUID пользователя, его роли.
     * @return Экземпляр {@link JwtAuthentication} с установленными правами доступа и информацией о пользователе.
     */
    public JwtAuthentication createAuthentication (Claims claims){
        UUID userUuid = UUID.fromString(claims.getSubject());

        List<?> rawRoles = claims.get("roles", List.class);

        Set<String> roles = rawRoles.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        Set<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new JwtAuthentication(userUuid, authorities);
    }
}
