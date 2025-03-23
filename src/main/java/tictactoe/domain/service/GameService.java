package tictactoe.domain.service;

import tictactoe.domain.model.*;
import tictactoe.security.model.JwtAuthentication;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

public interface GameService {

    /**
     * Получает логин пользователя из текущего контекста безопасности.
     *
     * @return Логин пользователя, извлечённый из {@link JwtAuthentication}.
     */
    String getUserLogin();


    /**
     * Получение игры из базы данных игр game_storage по уникальному идентификатору
     *
     * @param uuid Уникальный идентификатор игры
     * @return Игра из базы данных игр game_storage
     */
    Game getGame(UUID uuid);

    /**
     * Получение UUID игры, находящейся в статусе ожидание или игровой процесс, по UUID игрока
     *
     * @param userUuid Уникальный идентификатор пользователя
     * @return Активная игра с искомым пользователем
     */
    UUID activeGame(UUID userUuid);

    /**
     * Пустой ли список игр, находящихся в статусе ожидание и имеющих знак, противоположный выбранному
     *
     * @param playerChoice Выбранный игроком, делющим запрос, игровой знак
     * @return true - список пуст, false - нет
     */
    boolean noNewGames(String playerChoice);

    /**
     * Получение списка UUID игр, которые находятся в статусе ожидание и имеющих знак, противоположный выбранному
     *
     * @param playerChoice Выбранный игроком, делющим запрос, игровой знак
     * @return Список UUID найденных игр
     */
    List<UUID> waitGames(String playerChoice);

    /**
     * Проверка игры на возможность начала вторым игроком
     *
     * @param gameUuid         UUID проверяемой игры
     * @param secondPlayerUuid UUID подсоединяющегося к игре игрока
     * @return true - игра доступна для начала, false - нет
     */
    boolean prepareGameForSecondPlayer(String gameUuid, UUID secondPlayerUuid);

    /**
     * Создание новой игры в базе данных игр game_storage
     *
     * @param playerChoice  Выбор игрового знака игроком (1 - крестик, 2 - нолик)
     * @param playerOneUuid Уникальный идентификатор первого игрока
     * @param state         Состояние новой игры
     * @return Новая игра
     */
    Game initServerGame(String playerChoice, UUID playerOneUuid, GameState state);

    /**
     * Обновление игры в базе данных игр game_storage
     *
     * @param clientGame Обновленная игра
     */
    void updateGame(Game clientGame);


    /**
     * Проверка статуса игры в зависимости от расстановки ходов на поле и ход сервера при продолжении игры
     *
     * @param dataGame       Игровое поле без хода сервера
     * @param cellIndex      Индекс кнопки, которую выбрал клиент в качестве собственного хода
     * @param movePlayerUuid UUID пользователя, делающего ход (не обязательно его ход сейчас)
     * @return Объект класса MoveInfo, содержащий обновленное игровое поле и сопутсвующее сообщение о состоянии игры
     */
    CompletableFuture<MoveInfo> checkMove(Game dataGame, int cellIndex, UUID movePlayerUuid);

    /**
     * Проверка на сохранность поля(прошлых ходов) после текущего хода
     *
     * @param boardOld   Поле до последнего хода
     * @param moveRow    Строка, на которой находится ход игрока
     * @param moveColumn Столбец, на котором находится ход игрока
     * @return true - поле в порядке, ничего не нарушено, false - есть нарушения порядка
     */
    boolean boardValidation(GameBoard boardOld, int moveRow, int moveColumn);

    /**
     * Метод для проверки окончания игры
     *
     * @param board Поле
     * @return true - игра окончена, false - игра продолжается
     */
    boolean isGameOver(GameBoard board);

    /**
     * Проверка поля на победу (закрашено необходимое количество клеток одним символом?)
     *
     * @param board Поле
     * @return true - победа, false - нет
     */
    boolean isWin(GameBoard board);

    /**
     * Метод для определения ничьи по поиску свободных клеток на поле (со значением 0)
     *
     * @param board Поле
     * @return true - свободных клеток нет (ничья), false - место для хода еще есть
     */
    boolean isTie(GameBoard board);

    /**
     * Получение знака-победителя
     *
     * @param dataGame Текущая игра
     * @return Строка с победителем (крестики/нолики)
     */
    String getWinnerName(Game dataGame);

    /**
     * Метод удаления игры из базы данных game_storage
     *
     * @param gameUuid UUID удаляемой игры
     */
    void deleteGame(UUID gameUuid);

    /**
     * Метод получения игровой статистики по завершенным играм по UUID пользователя из базы данных game_storage
     *
     * @param userUuid UUID игрока
     * @return Объект класса UserStatsDTO, содержащий статистику по завершенным играм выбранного игрока
     */
    UserStatsDTO getUsersStatsByUuid(UUID userUuid);

    /**
     * Получает UUID пользователя из текущего контекста безопасности.
     *
     * @return UUID пользователя, извлечённый из текущей аутентификации.
     */
    UUID getUserUuid();

    /**
     * Получает список завершённых игр (ничьи или победы) для текущего пользователя.
     *
     * @return Список объектов {@link Game}, представляющих завершённые игры текущего пользователя.
     */
    List<Game> getUserEndGames();

    /**
     * Получает список лучших игроков с их соотношением побед.
     *
     * @param leadersCount Строковое значение, представляющее количество лучших игроков, которые должны быть получены.
     * @return Список объектов {@link UserWinRate}, представляющих лучших игроков и их соотношение побед.
     */
    List<UserWinRate> getLeadersList(String leadersCount);


    /**
     * Возвращает выбор символа игрока и тип противника в текущей игре.
     * <p>
     * Метод определяет, какой символ (X или O) использует игрок с заданным UUID,
     * а также определяет, является ли противник компьютером или человеком.
     * </p>
     *
     * @param userUuid UUID пользователя, для которого определяется выбор символа.
     * @param gameUuid UUID игры, из которой извлекается информация.
     * @return {@code Map<String, String>} с информацией о выборе символа и типе противника:
     * <ul>
     *     <li>{@code "playerChoice"} - Символ игрока: "X" или "O".</li>
     *     <li>{@code "opponent"} - Тип противника: "computer" или "human".</li>
     * </ul>
     */
    public Map<String, String> getPlayerChoiceByUserUuid(UUID userUuid, UUID gameUuid);
}
