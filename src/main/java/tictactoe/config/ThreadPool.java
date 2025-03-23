package tictactoe.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    /**
     * Пул потоков
     */
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Возвращает экземпляр пула потоков
     *
     * @return Экземпляр ExecutorService
     */
    public static ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Завершает работу пула потоков
     */
    public static void shutdown() {
        executor.shutdown();
    }
}
