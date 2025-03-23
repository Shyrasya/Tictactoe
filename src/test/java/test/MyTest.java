package test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tictactoe.TicTacToeApplication;
import tictactoe.domain.service.GameService;
import tictactoe.domain.model.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = TicTacToeApplication.class)
public class MyTest {
    @Autowired
    private GameService gameService;

    /**
     * Тест для проверки многопоточности в domain-слое (gameService.checkServerMove)
     */
    @Test
    void concurrentGamesTest() throws ExecutionException, InterruptedException {
        int cellIndex = 1;
        int numTasks = 100;
        @SuppressWarnings("unchecked")
        CompletableFuture<MoveInfo>[] futures = (CompletableFuture<MoveInfo>[]) new CompletableFuture[numTasks];
        for (int i = 0; i < numTasks; i++) {
            futures[i] = createGameTask(cellIndex);
        }
        CompletableFuture.allOf(futures).join();
        for (CompletableFuture<MoveInfo> future : futures) {
            MoveInfo result = future.get();
            assertNotNull(result);
        }
    }

    /**
     * Создание задачи (ход компьтера на основе хода клиента) для потока
     *
     * @param cellIndex Нажатая клиентом клавиша на поле
     * @return Объект класса ServerMoveInfo с информацией о статусе хода и координатой хода сервера
     */
    private CompletableFuture<MoveInfo> createGameTask(int cellIndex) {
        return CompletableFuture.supplyAsync(() -> {
            Game dataGame = gameService.initServerGame("X", null, GameState.GAME);
            dataGame.getBoard().setElement(0, 0, 1);
            try {
                return gameService.checkMove(dataGame, cellIndex, null).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Тест на проверку блокирования других запросов при обращении к одной игре
     * в процессе выполнения метода domain (gameService.checkServerMove)
     */
    @Test
    void testCheckServerMoveSynchronization() throws InterruptedException {
        int cellIndex = 1;
        int numThreads = 3;
        Game game = gameService.initServerGame("X", null, GameState.GAME);
        game.getBoard().setElement(0, 0, 1);

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    latch.await();
                    MoveInfo result = gameService.checkMove(game, cellIndex, null).get();
                    assertNotNull(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        latch.countDown();
        executorService.shutdown();
        if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
            throw new IllegalStateException("Tasks did not complete in the expected time.");
        }
    }
}
