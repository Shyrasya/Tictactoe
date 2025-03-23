package tictactoe.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tictactoe.datasource.repository.UserRepository;
import tictactoe.domain.service.UserService;
import tictactoe.domain.service.impl.UserServiceImpl;
import tictactoe.security.JwtProvider;
import tictactoe.security.JwtUtil;
import tictactoe.web.filter.AuthFilter;

/**
 * Конфигурация Spring для игрового приложения
 * Определяет beans для управления безопасностью приложения
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Сервис, реализующий бизнес-логику для работы с данными пользователей
     *
     * @param userRepository Data-sorce-часть для работы с таблицей данных пользователей
     * @param provider Cервис для работы с JWT-токенами (генерация, валидация)
     * @param util Объект класса {@link JwtUtil}, предоставляющий утилитарный метод для работы с JWT
     * - создание объекта аутентификации на основе данных из токена
     * @return Новый сервис
     */
    @Bean
    public UserServiceImpl userService(UserRepository userRepository, JwtProvider provider, JwtUtil util) {
        return new UserServiceImpl(userRepository, provider, util);
    }

    /**
     * Создаёт и предоставляет экземпляр {@link JwtProvider}.
     * <p>
     * Используется для генерации и валидации JWT-токенов в приложении.
     * </p>
     *
     * @return Экземпляр {@link JwtProvider}.
     */
    @Bean
    public JwtProvider provider(){
        return new JwtProvider();
    }

    /**
     * Создаёт и предоставляет экземпляр {@link JwtUtil}.
     * <p>
     * Используется для выполнения операций с JWT, таких как разбор и проверка токенов.
     * </p>
     *
     * @return Экземпляр {@link JwtUtil}.
     */
    @Bean
    public JwtUtil util(){
        return new JwtUtil();
    }


    /**
     * Кастомный фильтр, валидирующий учетные данные пользователя
     *
     * @param userService Сервис, реализующий бизнес-логику для работы с данными пользователей
     * @return Новый фильтр {@link AuthFilter}
     */
    @Bean
    public AuthFilter authFilter(UserService userService) {
        return new AuthFilter(userService);
    }

    /**
     * Конфигурирует цепочку фильтров безопасности для приложения
     *
     * @param http       Центральный компонент Spring Security, который используется для настройки безопасности веб-приложения
     * @param authFilter Пользовательский фильтр аутентификации, который добавляется в цепочку фильтров
     * @return Настроенная цепочка фильтров безопасности
     * @throws Exception В случае возникновения ошибок во время конфигурации безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthFilter authFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/tictactoe/auth/login",
                                "/tictactoe/auth/register",
                                "/tictactoe/auth/update-access",
                                "/favicon.ico",
                                "/*.html",
                                "/js/*.js",
                                "/ws/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
