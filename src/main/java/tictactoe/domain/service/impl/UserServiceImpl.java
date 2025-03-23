package tictactoe.domain.service.impl;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tictactoe.datasource.mapper.UserMapper;
import tictactoe.domain.model.*;
import tictactoe.datasource.repository.UserRepository;
import tictactoe.domain.service.UserService;
import tictactoe.security.JwtProvider;
import tictactoe.security.JwtUtil;
import tictactoe.security.model.JwtAuthentication;

import java.util.Collections;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    /**
     * Репозиторий для работы с учетными данными пользователя
     */
    private final UserRepository userRepository;

    /**
     * Объект класса {@link JwtProvider}, предоставляет методы для генерации, валидации и извлечения информации из JSON Web Tokens (JWT)
     */
    private final JwtProvider provider;

    /**
     * Объект класса {@link JwtUtil}, предоставляющий утилитарный метод для работы с JWT
     * - создание объекта аутентификации на основе данных из токена
     */
    private final JwtUtil util;

    /**
     * Перечисление типов токенов.
     * <p>
     * Доступные типы:
     * <ul>
     *     <li>{@code ACCESS} — Токен доступа, используемый для аутентификации запросов.</li>
     *     <li>{@code REFRESH} — Токен обновления, используемый для получения нового токена доступа.</li>
     * </ul>
     */
    public enum TokenType {
        ACCESS, REFRESH
    }

    /**
     * Параметризированный конструктор класса GameServiceImpl
     *
     * @param userRepository Репозиторий для работы с учетными данными пользователя
     * @param provider       Cервис для работы с JWT-токенами (генерация, валидация)
     * @param util Объект класса {@link JwtUtil}, предоставляющий утилитарный метод для работы с JWT
     * - создание объекта аутентификации на основе данных из токена
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtProvider provider, JwtUtil util) {
        this.userRepository = userRepository;
        this.provider = provider;
        this.util = util;
    }

    /**
     * Логика регистрации нового пользователя
     *
     * @param signUpRequest Форма с логином и паролем регистрирующегося пользователя
     * @return Успешная регистрация (true - да, false - нет)
     */
    @Override
    @Transactional
    public boolean newUserRegistration(SignUpRequest signUpRequest) {
        boolean newRegistration;
        if (userRepository.existsByLogin(signUpRequest.getLogin()))
            newRegistration = false;
        else {
            User newUser = new User();
            newUser.setLogin(signUpRequest.getLogin());

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
            newUser.setPassword(encodedPassword);

            newUser.setUuid(UUID.randomUUID());
            newUser.setRoles(Collections.singleton(Role.USER));
            userRepository.save(UserMapper.fromDomainToDataSource(newUser));
            newRegistration = true;
        }
        return newRegistration;
    }

    /**
     * Валидация введенных учетных данных пользователя для входа в систему
     *
     * @param request request объект класса {@link JwtRequest}, содержащий логин и пароль пользователя
     * @return объект {@link JwtResponse}, содержащий сгенерированные JWT-токены (access и refresh)
     */
    @Override
    @Transactional
    public JwtResponse authorization(JwtRequest request) {
        String inputLogin = request.getLogin();
        String inputPassword = request.getPassword();
        tictactoe.datasource.model.User dataSourceUser = userRepository.findByLogin(inputLogin);
        if (dataSourceUser == null) {
            throw new BadCredentialsException("Неверные данные!");
        }
        User user = UserMapper.fromDataSourceToDomain(dataSourceUser);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(inputPassword, user.getPassword())){
            throw new BadCredentialsException("Неверные данные!");
        }
        String accessToken = provider.generateAccessToken(user);
        String refreshToken = provider.generateRefreshToken(user);
        userRepository.updateRefreshTokenByUuid(user.getUuid(), refreshToken);
        return new JwtResponse(accessToken, refreshToken, getExpiresAccessToken(accessToken));
    }

    /**
     * Получение срока годности access-токена
     * @param accessToken Токен доступа
     * @return Время истечения срока годности access-токена (Unix timestamp (в секундах) - время в секундах с начала эпохи)
     */
    private long getExpiresAccessToken(String accessToken){
        Claims accessClaims = provider.getClaims(accessToken);
        return accessClaims.getExpiration().getTime() / 1000;
    }

    /**
     * Метод обновления access-токена
     * @param refreshToken Токен обновления
     * @return Объект класса {@link JwtResponse}, содержащий обновленный access, refresh-токен и время годности нового access-токена
    */
    @Override
    public JwtResponse updateAccessToken(String refreshToken){
        return updateToken(refreshToken, TokenType.ACCESS);
    }

    /**
     * Метод обновления refresh-токена
     * @param refreshToken Токен обновления
     * @return Объект класса {@link JwtResponse}, содержащий обновленный access, refresh-токен и время годности нового access-токена
    */
    @Override
    public JwtResponse updateRefreshToken(String refreshToken){
        return updateToken(refreshToken, TokenType.REFRESH);
    }


    /**
     * Метод обновления токена
     * @param refreshToken Токен обновления
     * @return Объект класса {@link JwtResponse}, содержащий обновленный access, refresh-токен и время годности нового access-токена
     * @throws ResponseStatusException если произошла ошибка при обработке запроса:
     * <ul>
     *  <li> UNAUTHORIZED — если предоставленный токен поддельный.</li>
     *  <li> FORBIDDEN — если срок годности refresh-токена истек.</li>
     *  <li> INTERNAL_SERVER_ERROR — если произошла внутренняя ошибка сервера.</li>
     *  </ul>
     */
    @Transactional
    private JwtResponse updateToken(String refreshToken, TokenType tokenType){
        try {
            Claims claims = provider.validateToken(refreshToken);
            UUID userUuid;
            try{
                userUuid = UUID.fromString(claims.getSubject());
            } catch (IllegalArgumentException e){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Некорректный UUID пользователя в токене!");
            }
            tictactoe.datasource.model.User dataUser = userRepository.findByUuid(userUuid);
            if (dataUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Такого пользователя не существует!");
            }
            User user = UserMapper.fromDataSourceToDomain(dataUser);
            if (!provider.validateRefreshToken(user, refreshToken)){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Токен не совпадает с бд!");
            }

            if (tokenType == TokenType.ACCESS){
                if (!provider.isFreshToken(claims))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Срок годности refresh-токена истек!");
            } else {
                refreshToken = provider.generateRefreshToken(user);
                userRepository.updateRefreshTokenByUuid(user.getUuid(), refreshToken);
            }

            String newAccessToken = provider.generateAccessToken(user);
            long expiresIn = getExpiresAccessToken(newAccessToken);

            return new JwtResponse(newAccessToken, refreshToken, expiresIn);
        } catch (SecurityException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Поддельный токен!");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Серверная ошибка обновления refresh-токена!", e);
        }
    }

    /**
     * Валидирует переданный access-токен с использованием провайдера токенов.
     *
     * @param accessToken Access-токен для валидации.
     * @return Объект {@link Claims}, содержащий информацию из валидного токена.
     */
    @Override
    public Claims providerValidateAccessToken(String accessToken){
        return provider.validateAccessToken(accessToken);
    }

    /**
     * Создаёт объект {@link JwtAuthentication} на основе переданных данных из токена.
     *
     * @param claims Информация из токена, извлечённая после его валидации.
     * @return Объект {@link JwtAuthentication}, содержащий данные о пользователе.
     */
    @Override
    public JwtAuthentication getJwtAuthentication(Claims claims){
        return util.createAuthentication(claims);
    }

    /**
     * Извлекает UUID текущего авторизованного пользователя из {@link SecurityContextHolder}.
     *
     * @return UUID текущего пользователя.
     */
    private UUID getUserUuid(){
        Authentication jwtAuth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(jwtAuth.getName());
    }

    /**
     * Удаляет refresh-токен текущего авторизованного пользователя из базы данных.
     */
    @Override
    @Transactional
    public void deleteRefreshToken(){
        UUID userUuid = getUserUuid();
        userRepository.deleteRefreshTokenByUuid(userUuid);
    }

    public JwtProvider getProvider() {
        return provider;
    }

    public JwtUtil getUtil() {
        return util;
    }
}
