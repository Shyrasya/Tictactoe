package tictactoe.security;

import io.jsonwebtoken.*;
import tictactoe.domain.model.User;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

public class JwtProvider {
    private static final long EXPIRATION_TIME_ACCESS_TOKEN = 1000 * 60 * 10; // 10 минут
    private static final long EXPIRATION_TIME_REFRESH_TOKEN = 1000 * 60 * 60 * 24 * 7; // 7 дней
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    /**
     * Генерирует JWT токен доступа для указанного пользователя.
     *
     * @param user Пользователь, для которого создаётся токен.
     * @return Сгенерированный токен доступа в виде строки.
     */
    public String generateAccessToken(User user){
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(EXPIRATION_TIME_ACCESS_TOKEN);

        return Jwts.builder()
                .subject(user.getUuid().toString())
                .claim("roles", user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .claim("login", user.getLogin())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Генерирует JWT токен обновления для указанного пользователя.
     *
     * @param user Пользователь, для которого создаётся токен.
     * @return Сгенерированный токен обновления в виде строки.
     */
    public String generateRefreshToken(User user){
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(EXPIRATION_TIME_REFRESH_TOKEN);

        return Jwts.builder()
                .subject(user.getUuid().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Проверяет валидность переданного JWT токена доступа.
     *
     * @param accessToken Токен доступа, который необходимо проверить.
     * @return Объект {@link Claims}, содержащий информацию о токене, если он валиден, иначе null.
     */
    public Claims validateAccessToken(String accessToken) {
        try {
            return validateToken(accessToken);
        } catch(SecurityException e){
            return null;
        }
    }

    /**
     * Сравнение токена из claims от user(от refresh-токена) и токена из бд
     * @param user Пользователь, от которого был зашифрован refresh-токен
     * @param refreshToken refresh-токен из бд
     * @return true - токены равны, false - нет
     */
    public boolean validateRefreshToken(User user, String refreshToken){
        String userRefreshToken = user.getRefreshToken();
        return userRefreshToken.equals(refreshToken);
    }

    /**
     * Метод декодирования Claims из токена с защитой исключений для дальнейшей обработки
     * @param token Принимаемый токен
     * @return {@link io.jsonwebtoken.Claims} - данные из токена
     * @throws SecurityException Если токен некорректный
     */
    public Claims validateToken(String token) {
        try {
            return getClaims(token);
        } catch (SecurityException e){
            throw new SecurityException("Неверная подпись JWT!", e);
        } catch (MalformedJwtException e){
            throw new SecurityException("Некорректный JWT!", e);
        } catch (JwtException e){
            throw new SecurityException("Ошибка в JWT!", e);
        }
    }

    /**
     * Декодирование данных из токена
     * @param token Принимаемый токен
     * @return Claims-данные из токена
     */
    public Claims getClaims(String token){
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        } catch (Exception e){
            throw new SecurityException("Ошибка при декодировании токена", e);
        }
    }

    /**
     * Проверка годности токена по claims
     * @param claims Декодированные данные из токена
     * @return true - токен годен, false - нет
     */
    public boolean isFreshToken(Claims claims){
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}
