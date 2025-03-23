package tictactoe.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tictactoe.domain.service.UserService;
import tictactoe.web.mapper.JwtRequestMapper;
import tictactoe.web.mapper.JwtResponseMapper;
import tictactoe.web.mapper.SignUpRequestMapping;
import tictactoe.web.model.JwtRequest;
import tictactoe.web.model.JwtResponse;
import tictactoe.web.model.SignUpRequest;

@Controller
@RequestMapping("/tictactoe/auth")
public class AuthorizationController {

    /**
     * Domain-часть для работы с логической частью приложения - пользователями игры
     */
    private final UserService userService;

    /**
     * Параметризированный конструктор класса AuthorizationController
     *
     * @param userService domain-часть для работы с пользователями приложения
     */
    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Обработка get-запроса для /tictactoe/auth/login - показ страницы с авторизацией пользователя
     *
     * @return Открытие authorization.html по адресу /tictactoe/auth/login
     */
    @GetMapping("/login")
    public String showAuthorizationPage() {
        return "forward:/authorization.html";
    }

    /**
     * Обрабатывает POST-запрос для авторизации пользователя.
     * <p>
     * Запрос принимает учетные данные пользователя (логин и пароль), проверяет их на корректность и,
     * в случае успеха, возвращает access-токен и refresh-токен. В случае ошибки возвращает код 401.
     * </p>
     *
     * @param request {@code JwtRequest} с учетными данными пользователя
     * @return {@code ResponseEntity<JwtResponse>} с результатом авторизации:
     * <ul>
     *     <li>{@code HTTP 200 (OK)} - Успешная авторизация и возвращение токенов.</li>
     *     <li>{@code HTTP 401 (Unauthorized)} - Неправильные учетные данные (неверный логин или пароль).</li>
     * </ul>
     */
    @PostMapping("/login")
    public ResponseEntity<?> validCredentialsGameRedirect(@RequestBody JwtRequest request) {
        try {
            tictactoe.domain.model.JwtRequest domainRequest = JwtRequestMapper.fromWebToDomain(request);
            JwtResponse response = JwtResponseMapper.fromDomainToWeb(userService.authorization(domainRequest));
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Обработка get-запроса для /tictactoe/auth/register - показ страницы с регистрацией пользователя
     *
     * @return Открытие registration.html по адресу /tictactoe/auth/register
     */
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "registration";
    }

    /**
     * Обработка post-запроса для /tictactoe/auth/register - проверка введенных данных на отсутствие в базе пользователей
     *
     * @param activeButton Нажатая кнопка регистрации или возврата к странице авторизации
     * @param login        Введенный пользователем логин
     * @param password     Введенный пользователем пароль
     * @param model        Контейнер, который используется для передачи информации, необходимой для отображения страницы, клиенту от сервера
     * @return Вывод на странице /tictactoe/auth/register соответсвующего сообщения об уcпешности создания нового пользователя
     */
    @PostMapping("/register")
    public String checkRegistration(@RequestParam String activeButton,
                                    @RequestParam("login") String login,
                                    @RequestParam("password") String password,
                                    Model model) {
        if ("return-to-authorization".equals(activeButton))
            return "redirect:/tictactoe/auth/login";
        SignUpRequest signUpRequest = new SignUpRequest(login, password);
        if (userService.newUserRegistration(SignUpRequestMapping.fromWebToDomain(signUpRequest))) {
            model.addAttribute("message", "Новый пользователь " + login + " успешно зарегистрирован!");
        } else
            model.addAttribute("message", "Пользователь уже существует!");
        return "registration";
    }


    /**
     * Обрабатывает POST-запрос для обновления access-токена.
     * <p>
     * Запрос принимает refresh-токен клиента, проверяет его и выдает новый access/refresh токены.
     * В случае успешного обновления возвращается новый access-токен с временем истечения.
     * </p>
     *
     * @param refreshToken Refresh-токен клиента
     * @return {@code ResponseEntity<JwtResponse>} с результатом обновления:
     * <ul>
     *     <li>{@code HTTP 200 (OK)} - Новый access/refresh токены и время истечения access-токена.</li>
     *     <li>{@code HTTP 401 (Unauthorized)} - Неправильный refresh-токен.</li>
     *     <li>{@code HTTP 403 (Forbidden)} - Refresh-токен просрочен.</li>
     *     <li>{@code HTTP 500 (Internal Server Error)} - Ошибка сервера.</li>
     * </ul>
     */
    @PostMapping("/update-access")
    public ResponseEntity<?> updateAccessToken(@RequestBody String refreshToken){
        try {
            JwtResponse response = JwtResponseMapper.fromDomainToWeb(userService.updateAccessToken(refreshToken));
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Обрабатывает POST-запрос для обновления refresh-токена.
     * <p>
     * Запрос принимает refresh-токен клиента и возвращает новый refresh-токен, если старый является допустимым.
     * </p>
     *
     * @param refreshToken Refresh-токен клиента
     * @return {@code ResponseEntity<JwtResponse>} с результатом обновления:
     * <ul>
     *     <li>{@code HTTP 200 (OK)} - Новый refresh-токен.</li>
     *     <li>{@code HTTP 401 (Unauthorized)} - Неправильный refresh-токен.</li>
     *     <li>{@code HTTP 500 (Internal Server Error)} - Ошибка сервера.</li>
     * </ul>
     */
    @PostMapping("/update-refresh")
    public ResponseEntity<?> updateRefreshToken(@RequestBody String refreshToken){
        try {
            JwtResponse response = JwtResponseMapper.fromDomainToWeb(userService.updateRefreshToken(refreshToken));
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Обрабатывает POST-запрос для выхода пользователя.
     * <p>
     * Запрос удаляет refresh-токен пользователя, очищает контекст безопасности и сообщает о выходе.
     * </p>
     *
     * @return {@code ResponseEntity<String>} с результатом выхода:
     * <ul>
     *     <li>{@code HTTP 200 (OK)} - Пользователь успешно вышел.</li>
     *     <li>{@code HTTP 500 (Internal Server Error)} - Ошибка при выходе.</li>
     * </ul>
     */
    @PostMapping("/logout")
    public ResponseEntity<?> userLogout(){
        try {
            userService.deleteRefreshToken();
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok("Пользователь успешно вышел!");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при выходе: " + e.getMessage());
        }
    }
}
