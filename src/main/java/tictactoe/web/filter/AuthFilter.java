package tictactoe.web.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import tictactoe.domain.service.UserService;
import tictactoe.security.model.JwtAuthentication;

import java.io.IOException;

public class AuthFilter extends GenericFilterBean {

    /**
     * Domain-часть для работы с логической частью приложения - пользователями игры
     */
    private final UserService userService;

    /**
     * Параметризированный конструктор класса Auth Filter
     *
     * @param userService domain-часть для работы с пользователями приложения
     */
    public AuthFilter(UserService userService) {
        this.userService = userService;
    }

    /**
     * Фильтр аутентификации и авторизации пользователя в игре
     *
     * @param request  Содержит данные запроса от клиента
     * @param response Позволяет отправлять данные клиенту
     * @param chain    Цепочка фильтров, предоставляющая доступ к следующему фильтру в цепочке,
     *                 чтобы этот фильтр передал запрос и ответ для дальнейшей обработки
     * @throws IOException      В случае возникновения ошибок ввода-вывода при обработке запроса
     * @throws ServletException В случае возникновения ошибок, связанных с сервлетами
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        if (path.equals("/tictactoe/auth/login") ||
                path.equals("/tictactoe/auth/register") ||
                path.equals("/tictactoe/auth/update-access") ||
                path.equals("/favicon.ico") ||
                path.endsWith(".html") ||
                path.endsWith(".js") ||
                path.startsWith("/ws")
        ) {
            chain.doFilter(request, response);
            return;
        }

        String header = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")){
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Необходима аутентификация!");
            return;
        }
        String accessToken = header.substring(7);
        Claims claims = userService.providerValidateAccessToken(accessToken);
        if (claims == null){
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Невалидный access-токен!");
            return;
        }
        JwtAuthentication authentication = userService.getJwtAuthentication(claims);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
