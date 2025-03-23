package tictactoe.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import tictactoe.datasource.repository.GameRepository;
import tictactoe.domain.service.impl.GameServiceImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Конфигурация Spring для игрового приложения
 * Определяет beans для управления игровыми данными
 */
@Configuration
@EnableAsync
public class SpringConfiguration {

    /**
     * Сервис, реализующий бизнес-логику для управления играми
     *
     * @param gameRepository Репозиторий для доступа к данным об играх
     * @return Новый сервис
     */
    @Bean
    public GameServiceImpl gameService(GameRepository gameRepository) {
        return new GameServiceImpl(gameRepository);
    }

    /**
     * Создает и возвращает пул потоков для многопоточных задач
     *
     * @return Пул потоков (Executor), который управляет потоками для выполнения задач
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        return Executors.newCachedThreadPool();
    }
}
