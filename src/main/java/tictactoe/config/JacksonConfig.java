package tictactoe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки {@link ObjectMapper}, используемого для сериализации и десериализации JSON.
 */
@Configuration
public class JacksonConfig {

    /**
     * Создаёт и настраивает экземпляр {@link ObjectMapper} с поддержкой модулей для работы с JavaTime
     * и отключением сериализации дат как временных меток.
     *
     * @return Настроенный {@code ObjectMapper}
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}



