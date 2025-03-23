package tictactoe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигурационный класс для настройки WebSocket и брокера сообщений.
 * <p>
 * Этот класс использует аннотации {@code @Configuration} и {@code @EnableWebSocketMessageBroker}
 * для активации WebSocket-сообщений и настройки брокера сообщений.
 * Реализует интерфейс {@link WebSocketMessageBrokerConfigurer} для определения маршрутов и точек соединения.
 * </p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    /**
     * Регистрирует конечную точку "/ws" для подключения WebSocket клиентов, использующих протокол STOMP
     *
     * @param registry Реестр конечных точек STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    /**
     * Настраивает брокер сообщений для обработки STOMP сообщений.
     * Включает простой брокер, который передает сообщения подписчикам канала "/topic".
     * Устанавливает префикс "/app" для назначения сообщений, отправляемых от клиентов на сервер.
     *
     * @param registry Реестр для конфигурации брокера сообщений
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}

