package tictactoe.domain.service;

import io.jsonwebtoken.Claims;
import org.springframework.web.server.ResponseStatusException;
import tictactoe.domain.model.JwtRequest;
import tictactoe.domain.model.JwtResponse;
import tictactoe.domain.model.SignUpRequest;
import tictactoe.security.model.JwtAuthentication;

public interface UserService {

    /**
     * Логика регистрации нового пользователя
     *
     * @param signUpRequest Форма с логином и паролем регистрирующегося пользователя
     * @return Успешная регистрация (true - да, false - нет)
     */
    boolean newUserRegistration(SignUpRequest signUpRequest);


    /**
     * Валидация введенных учетных данных пользователя для входа в систему
     *
     * @param request request объект класса {@link JwtRequest}, содержащий логин и пароль пользователя
     * @return объект {@link JwtResponse}, содержащий сгенерированные JWT-токены (access и refresh)
     */
    JwtResponse authorization(JwtRequest request);


    /**
     * Метод обновления access-токена
     * @param refreshToken Токен обновления
     * @return Объект класса {@link JwtResponse}, содержащий обновленный access, refresh-токен и время годности нового access-токена
     * @throws ResponseStatusException если произошла ошибка при обработке запроса:
     * <ul>
     *  <li> UNAUTHORIZED — если предоставленный токен поддельный.</li>
     *  <li> FORBIDDEN — если срок годности refresh-токена истек.</li>
     *  <li> INTERNAL_SERVER_ERROR — если произошла внутренняя ошибка сервера.</li>
     *  </ul>
     */
    JwtResponse updateAccessToken(String refreshToken) throws Exception;

    /**
     * Метод обновления refresh-токена
     * @param refreshToken Токен обновления
     * @return Объект класса {@link JwtResponse}, содержащий обновленный access, refresh-токен и время годности нового access-токена
     * @throws ResponseStatusException если произошла ошибка при обработке запроса:
     * <ul>
     *  <li> UNAUTHORIZED — если предоставленный токен поддельный.</li>
     *  <li> INTERNAL_SERVER_ERROR — если произошла внутренняя ошибка сервера.</li>
     *  </ul>
     */
    JwtResponse updateRefreshToken(String refreshToken) throws Exception;

    /**
     * Создаёт объект {@link JwtAuthentication} на основе переданных данных из токена.
     *
     * @param claims Информация из токена, извлечённая после его валидации.
     * @return Объект {@link JwtAuthentication}, содержащий данные о пользователе.
     */
    JwtAuthentication getJwtAuthentication(Claims claims);

    /**
     * Валидирует переданный access-токен с использованием провайдера токенов.
     *
     * @param accessToken Access-токен для валидации.
     * @return Объект {@link Claims}, содержащий информацию из валидного токена.
     */
    Claims providerValidateAccessToken(String accessToken);

    /**
     * Удаляет refresh-токен текущего авторизованного пользователя из базы данных.
     */
    void deleteRefreshToken();
}
