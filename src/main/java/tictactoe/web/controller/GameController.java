package tictactoe.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tictactoe.domain.model.GameState;
import tictactoe.domain.service.GameService;
import tictactoe.web.exception.GlobalExceptionHandler;
import tictactoe.web.mapper.GameMapper;
import tictactoe.web.mapper.MoveInfoMapper;
import tictactoe.web.mapper.UserStatsDTOMapper;
import tictactoe.web.mapper.UserWinRateMapper;
import tictactoe.web.model.MoveInfo;
import tictactoe.web.exception.GameNotFoundException;
import tictactoe.web.model.Game;
import tictactoe.web.model.UserStatsDTO;
import tictactoe.web.model.UserWinRate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/tictactoe")
public class GameController {

    /**
     * Сервис, отвечающий за бизнес-логику игрового процесса.
     */
    private final GameService gameService;

    /**
     * Шаблон для отправки сообщений WebSocket клиентам.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Инструмент для сериализации и десериализации объектов JSON.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Логгер для регистрации событий и ошибок в приложении.
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Конструктор класса GameController
     *
     * @param gameService       Класс для общения с логической частью приложения
     * @param messagingTemplate Шаблон для отправки сообщений WebSocket клиентам
     */
    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Отображает главную стартовую страницу игры для пользователя.
     *
     * <p>Этот метод обрабатывает GET-запросы на URL /tictactoe/game и предоставляет основную страницу игры main.html.
     * Предполагается, что доступ к странице проходит через фильтрацию, обеспечивающую авторизацию пользователя.</p>
     *
     * @return ResponseEntity со статусом 200 (OK) в случае успешного выполнения запроса.
     */
    @GetMapping("/game")
    public ResponseEntity<Void> showMainPage() {
        return ResponseEntity.ok().build();
    }


    /**
     * Инициализация новой игры или подключение к существующей.
     *
     * <p>Этот метод обрабатывает POST-запросы на URL /tictactoe/game и позволяет пользователю начать новую игру
     * или подключиться к ожидающей игре другого игрока. Обрабатывается два варианта: игра с компьютером и игра с человеком.</p>
     *
     * <p><strong>Игра с компьютером:</strong> Немедленно создаётся новая игра и возвращается URL для этой игры.</p>
     *
     * <p><strong>Игра с человеком:</strong> Если нет ожидающих противников, создаётся новая игра в режиме ожидания.
     * В противном случае предоставляется URL для выбора игры из списка доступных игр.</p>
     *
     * @param payload Входные данные от клиента, содержащие:
     *                <ul>
     *                  <li><strong>opponent</strong> - выбранный противник ("computer" или "human").</li>
     *                  <li><strong>playerChoice</strong> - символ, выбранный пользователем для игры ("X" или "O").</li>
     *                </ul>
     * @return ResponseEntity с кодом и сопутствующими данными в формате JSON:
     *         <ul>
     *           <li><strong>200 (OK)</strong>: Успешное создание игры с компьютером, включая URL для игры.</li>
     *           <li><strong>201 (Created)</strong>: Игра с человеком создана и находится в режиме ожидания.</li>
     *           <li><strong>202 (Accepted)</strong>: Найден доступный противник, предоставлен URL для выбора игры.</li>
     *           <li><strong>208 (Already Reported)</strong>: Уже существует активная игра для текущего пользователя.</li>
     *           <li><strong>400 (Bad Request)</strong>: Некорректный запрос или ошибка в данных.</li>
     *         </ul>
     * @throws JsonProcessingException В случае ошибки сериализации/десериализации JSON.
     */
    @PostMapping("/game")
    public ResponseEntity<Map<String, Object>> initGame(@RequestBody Map<String, String> payload) throws JsonProcessingException {
        UUID userUuid = gameService.getUserUuid();
        UUID activeUserGameUuid = gameService.activeGame(userUuid);

        Map<String, Object> response = new HashMap<>();

        if (activeUserGameUuid != null) {
            response.put("activeGameUuid", activeUserGameUuid.toString());
            response.put("message", "Уже есть начатая игра!");
            Map<String, String> lastActiveGameChoice;
            lastActiveGameChoice = gameService.getPlayerChoiceByUserUuid(userUuid, activeUserGameUuid);
            response.put("playerChoice", lastActiveGameChoice.get("playerChoice"));
            response.put("opponent", lastActiveGameChoice.get("opponent"));
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
        }

        String opponent = payload.get("opponent");
        String playerChoice = payload.get("playerChoice");

        if ("computer".equals(opponent)) {
            UUID newGameUuid = initNewGame(playerChoice, GameState.GAME);
            response.put("gameUuid", newGameUuid.toString());
            return ResponseEntity.ok(response);
        }

        if ("human".equals(opponent)) {
            boolean noNewGames = gameService.noNewGames(playerChoice);
            if (noNewGames) {
                UUID newGameUuid = initNewGame(playerChoice, GameState.WAIT);
                sendUpdateForOpponent(playerChoice);
                response.put("gameUuid", newGameUuid.toString());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
            }
        }
        response.put("message", "Неверный запрос.");
        return ResponseEntity.badRequest().body(response);
    }


    /**
     * Смена пришедшего знака на противоположный для последующей отправки уведомления подписчикам веб-сокета
     *
     * @param playerChoice Текущий выбранный знак
     * @throws JsonProcessingException Если возникла проблема при преобразовании объекта в JSON строку
     */
    private void sendUpdateForOpponent(String playerChoice) throws JsonProcessingException {
        String opponentChoice = playerChoice.equals("X") ? "O" : "X";
        updateGameListForPlayer(opponentChoice);
    }

    /**
     * Поиск свободных игр для игры с учетом знака оппонента
     *
     * @param playerChoice Текущий знак для поиска свободных игр с противоположным знаком
     * @throws JsonProcessingException Если возникла проблема при преобразовании объекта в JSON строку
     */
    private void updateGameListForPlayer(String playerChoice) throws JsonProcessingException {
        List<UUID> freeGames = gameService.waitGames(playerChoice);
        messagingTemplate.convertAndSend("/topic/game-list/" + playerChoice, new ObjectMapper().writeValueAsString(freeGames));
    }


    /**
     * Отображает список доступных игр, ожидающих подключения противника.
     *
     * <p>Этот метод обрабатывает GET-запросы на URL /tictactoe/game/choice и предоставляет клиенту список игр,
     * находящихся в состоянии ожидания. Игры фильтруются в зависимости от символа, выбранного пользователем
     * (X или O), чтобы предотвратить конфликт при выборе одинаковых символов.</p>
     *
     * @param playerChoice Символ, выбранный пользователем для игры ("X" или "O").
     * @return ResponseEntity с JSON-ответом, содержащим:
     *         <ul>
     *           <li><strong>freeGames</strong> - Список UUID доступных игр, ожидающих подключения противника.</li>
     *           <li><strong>playerChoice</strong> - Символ, выбранный пользователем, переданный обратно для подтверждения.</li>
     *         </ul>
     */
    @GetMapping("/game/choice")
    public ResponseEntity<Map<String, Object>> showChoiceGamePage (@RequestParam("playerChoice") String playerChoice) {
        List<UUID> freeGames = gameService.waitGames(playerChoice);
        Map<String, Object> response = new HashMap<>();
        response.put("freeGames", freeGames);
        response.put("playerChoice", playerChoice);
        return ResponseEntity.ok(response);
    }

    /**
     * Обрабатывает POST-запрос на выбор игры для присоединения к существующей игре.
     *
     * Данный метод проверяет, свободна ли выбранная игра для второго игрока. Если игра еще не началась,
     * то игрок может присоединиться, и будет возвращена информация о перенаправлении на страницу игры.
     * В случае, если игра уже началась, возвращается сообщение об ошибке.
     *
     * @param gameUuid UUID игры, к которой пытается присоединиться второй игрок
     * @param playerChoice Выбор игрока (X или O), который будет присвоен второму игроку
     * @return ResponseEntity с результатом выполнения запроса:
     *         - HTTP статус 200 OK и URL игры, если игра свободна
     *         - HTTP статус 409 Conflict и сообщение об ошибке, если игра уже началась
     *
     * Статусы ответа:
     * <ul>
     *     <li><strong>200 OK</strong> - Если игра свободна, возвращается URL игры для перенаправления.</li>
     *     <li><strong>409 Conflict</strong> - Если игра уже началась, возвращается сообщение об ошибке.</li>
     * </ul>
     */
     @PostMapping("/game/choice")
    public ResponseEntity<Map<String, String>> joinGame(@RequestParam("gameUuid") String gameUuid, @RequestParam("playerChoice") String playerChoice) throws JsonProcessingException {
        UUID secondPlayerUuid = gameService.getUserUuid();
        boolean isFreeGame = gameService.prepareGameForSecondPlayer(gameUuid, secondPlayerUuid);
        Map<String, String> response = new HashMap<>();
        if (!isFreeGame) {
            response.put("message", "Игра уже началась. Выберите другую!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        updateGameListForPlayer(playerChoice);
        updateGameForFirstPlayer(gameUuid);
        response.put("gameUuid", gameUuid);
        return ResponseEntity.ok(response);
    }

    /**
     * Метод оповещения подписчиков веб-сокета об изменениях текущей игры по UUID
     *
     * @param gameUuid UUID игры
     * @throws JsonProcessingException Если возникла проблема при преобразовании объекта в JSON строку
     */
    private void updateGameForFirstPlayer(String gameUuid) throws JsonProcessingException {
        Game game = getWebGame(gameUuid);
        messagingTemplate.convertAndSend("/topic/game/" + gameUuid, objectMapper.writeValueAsString(game));
    }

    /**
     * Метод создания новой игры
     *
     * @param playerChoice Выбранный игрок знак
     * @param state        Игровое состояние игры
     * @return Новый uuid созданной игры
     */
    private UUID initNewGame(String playerChoice, GameState state) {
        UUID currentPlayerUuid = gameService.getUserUuid();
        Game newGame = GameMapper.fromDomainToWeb(gameService.initServerGame(playerChoice, currentPlayerUuid, state));
        return newGame.getUuid();
    }

    /**
     * Обрабатывает GET-запрос для получения информации об игре с указанным UUID.
     *
     * <p>В случае успешного запроса возвращает JSON-объект, содержащий:</p>
     * <ul>
     *     <li><strong>game</strong> — Объект {@link Game}, представляющий текущее состояние игры.</li>
     *     <li><strong>message</strong> — Сообщение о состоянии игры:
     *         <ul>
     *             <li>"Ожидание противника..." — если игра ожидает подключения второго игрока.</li>
     *             <li>"Ничья!" — если игра завершена вничью.</li>
     *             <li>"Победили [Имя игрока]" — если игра завершена победой одного из игроков.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * <p>Коды ответа:</p>
     * <ul>
     *     <li><strong>200 OK</strong> — Успешное получение данных игры.</li>
     *     <li><strong>404 Not Found</strong> — Игра с указанным UUID не найдена.</li>
     *     <li><strong>500 Internal Server Error</strong> — Внутренняя ошибка сервера.</li>
     * </ul>
     *
     * @param uuid Уникальный идентификатор игры.
     * @return {@link ResponseEntity} с JSON-объектом, содержащим данные игры и сообщение.
     */
    @GetMapping("/game/{uuid}")
    public ResponseEntity<?> showGamePage(@PathVariable("uuid") String uuid) {
        try {
            Game webGame = getWebGame(uuid);
            Map<String, Object> response = new HashMap<>();
            response.put("game", webGame);
            String message = switch(webGame.getState()){
                case WAIT -> "Ожидание противника...";
                case TIE -> "Ничья!";
                case WIN -> "Победили " + gameService.getWinnerName(GameMapper.fromWebToDomain(webGame));
                default -> "";
            };
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (GameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Обрабатывает POST-запрос для выполнения хода в игре "Крестики-нолики" по адресу /tictactoe/game/{uuid}.
     * <p>
     * Метод получает UUID текущей игры и индекс ячейки, выбранной игроком, для выполнения хода.
     * На основании хода обновляется состояние игры и отправляется уведомление через WebSocket, если игра продолжается.
     * <p>
     * В случае успешного выполнения возвращает JSON с информацией об игре и сообщением.
     * При возникновении ошибок возвращает соответствующее сообщение об ошибке и HTTP статус.
     *
     * @param gameUuid  Уникальный идентификатор игры (UUID), передаваемый в URL.
     * @param cellIndex Индекс выбранной ячейки (0-8) на игровом поле.
     * @return ResponseEntity с JSON-ответом, содержащим следующие ключи:
     *         <ul>
     *             <li><strong>message</strong> — Сообщение с результатом хода (например, ошибка или информация о ходе).</li>
     *             <li><strong>game</strong> — Обновлённое состояние игры ({@link Game}), если ход был успешным.</li>
     *         </ul>
     * <p>
     * HTTP статус ответа:
     * <ul>
     *     <li><strong>200 OK</strong> — Ход успешно выполнен, игра обновлена.</li>
     *     <li><strong>404 Not Found</strong> — Игра с указанным UUID не найдена.</li>
     *     <li><strong>500 Internal Server Error</strong> — Непредвиденная ошибка сервера.</li>
     * </ul>
     * <p>
     * WebSocket уведомления:
     * Если игра продолжается (состояние {@link tictactoe.web.model.GameState#GAME}) и получено сообщение о ходе,
     * обновлённая игра отправляется всем подписчикам по адресу /topic/game/{uuid}.
     */
    @PostMapping("/game/{uuid}")
    public ResponseEntity<Map<String, Object>> makeMove(@PathVariable("uuid") String gameUuid,
                           @RequestParam("cellIndex") int cellIndex) {
        Map<String, Object> response = new HashMap<>();
        try {
            Game webGame = getWebGame(gameUuid);
            UUID movePlayerUuid = gameService.getUserUuid();
            CompletableFuture<tictactoe.domain.model.MoveInfo> domainInfoFuture = gameService.checkMove(GameMapper.fromWebToDomain(webGame), cellIndex, movePlayerUuid);
            tictactoe.domain.model.MoveInfo domainInfo = domainInfoFuture.join();
            MoveInfo webInfo = MoveInfoMapper.fromDomainToWeb(domainInfo);
            response.put("message", webInfo.getMessage());
            response.put("game", webInfo.getGame());
            if (webGame.getState() == tictactoe.web.model.GameState.GAME &&
                    !Objects.equals(webInfo.getMessage(), "") &&
                    !Objects.equals(webInfo.getMessage(), "Ожидание противника!")) {
                messagingTemplate.convertAndSend("/topic/game/" + gameUuid, objectMapper.writeValueAsString(webInfo.getGame()));
            }

            return ResponseEntity.ok(response);
        } catch (GameNotFoundException e) {
            log.error("Ошибка при обработке игры", e);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Неизвестная ошибка", e);
            response.put("message", "Неизвестная ошибка: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Метод взятия игры из сервиса(domain) по uuid и переводу её в модель контроллера(web)
     *
     * @param uuid Уникальный идентификатор игры
     * @return Игра(web) из хранилища
     */
    private Game getWebGame(String uuid) {
        tictactoe.domain.model.Game domainGame = gameService.getGame(UUID.fromString(uuid));
        return GameMapper.fromDomainToWeb(domainGame);
    }

    /**
     * Обрабатывает POST-запрос для удаления игры по её UUID.
     * <p>
     * Удаление игры возможно только если игра находится в состоянии {@code WAIT}
     * и ещё не присоединился второй игрок. Также, удаление доступно только
     * игроку, который создал игру (игроку с идентификатором {@code playerOneUuid}).
     * </p>
     *
     * @param gameUuid Уникальный идентификатор игры.
     * @return {@code ResponseEntity<String>} с сообщением о результате операции:
     * <ul>
     *     <li>HTTP 200 (OK) - Игра успешно удалена.</li>
     *     <li>HTTP 403 (Forbidden) - У пользователя нет прав на удаление игры.</li>
     *     <li>HTTP 400 (Bad Request) - Игра не находится в состоянии ожидания.</li>
     * </ul>
     * @throws JsonProcessingException Если возникает ошибка при обработке JSON.
     */
    @PostMapping("/game/delete/{uuid}")
    public ResponseEntity<String> deleteGame(@PathVariable("uuid") String gameUuid) throws JsonProcessingException {
        Game webGame = getWebGame(gameUuid);
        if (webGame.getState() == tictactoe.web.model.GameState.WAIT && webGame.getPlayerTwoUuid() == null) {
            UUID userUuid = gameService.getUserUuid();
            if (webGame.getPlayerOneUuid().equals(userUuid)) {
                int signPlayerOne = webGame.getPlayerOneSign();
                String playerChoice = signPlayerOne == 1 ? "X" : "O";
                gameService.deleteGame(UUID.fromString(gameUuid));
                sendUpdateForOpponent(playerChoice);
                return ResponseEntity.ok("Игра успешно удалена");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав для удаления игры");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Игра не находится в состоянии ожидания");
    }

    /**
     * Обрабатывает GET-запрос для получения статистики завершённых игр пользователя.
     * <p>
     * Возвращает подробную статистику игр пользователя, включая общее количество игр,
     * количество побед, поражений и ничьих, а также процент побед в играх с компьютером и людьми.
     * </p>
     *
     * @param userUuid Уникальный идентификатор пользователя, для которого требуется получить статистику.
     * @return {@code UserStatsDTO} Объект, содержащий статистику по завершённым играм пользователя.
     */
    @GetMapping("/user/stats/{uuid}")
    @ResponseBody
    public UserStatsDTO getUsersStats(@PathVariable("uuid") String userUuid) {
        return UserStatsDTOMapper.fromDomainToWeb(gameService.getUsersStatsByUuid(UUID.fromString(userUuid)));
    }

    /**
     * Обрабатывает GET-запрос для получения всех завершенных игр текущего пользователя.
     * <p>
     * Игра считается завершенной, если её состояние — {@code TIE} (ничья) или {@code WIN} (победа).
     * UUID пользователя извлекается из {@code SecurityContextHolder}, установленного при фильтрации запроса.
     * </p>
     *
     * @return {@code ResponseEntity<Map<String, Object>>} с информацией о завершенных играх:
     * <ul>
     *     <li>HTTP 200 (OK) - Список завершенных игр под ключом {@code "endGames"}.</li>
     *     <li>HTTP 500 (Internal Server Error) - В случае ошибки, ключ {@code "error"} содержит сообщение об ошибке.</li>
     * </ul>
     */
    @GetMapping("/user/endGames")
    public ResponseEntity<Map<String, Object>> getUserEndGames(){
        Map<String, Object> response = new HashMap<>();
        try {
            List<Game> endGames = gameService.getUserEndGames()
                    .stream()
                    .map(GameMapper::fromDomainToWeb)
                    .toList();
            response.put("endGames", endGames);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Обрабатывает GET-запрос для получения информации о текущем пользователе.
     * <p>
     * Информация включает логин пользователя и его UUID, который извлекается из
     * {@code SecurityContextHolder}, установленного при фильтрации запроса.
     * </p>
     *
     * @return {@code ResponseEntity<Map<String, String>>} с информацией о пользователе:
     * <ul>
     *     <li>HTTP 200 (OK) - Информация о пользователе под ключами {@code "login"} и {@code "userUuid"}.</li>
     *     <li>HTTP 500 (Internal Server Error) - В случае ошибки, ключ {@code "error"} содержит сообщение об ошибке.</li>
     * </ul>
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, String>> getUserInfo(){
        Map<String, String> response = new HashMap<>();
        try {
            String login = gameService.getUserLogin();
            String userUuid = gameService.getUserUuid().toString();
            response.put("login", login);
            response.put("userUuid", userUuid);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * Обрабатывает GET-запрос для получения списка лидеров.
     * <p>
     * Метод запрашивает список лидеров на основе переданного количества и преобразует его
     * в веб-формат с использованием {@code UserWinRateMapper}.
     * </p>
     *
     * @param leadersCount строка, представляющая количество лидеров, которых необходимо получить.
     * @return {@code ResponseEntity<Map<String, Object>>} с информацией о списке лидеров:
     * <ul>
     *     <li>HTTP 200 (OK) - Список лидеров под ключом {@code "leadersList"} в виде списка объектов {@code UserWinRate}.</li>
     *     <li>HTTP 500 (Internal Server Error) - В случае ошибки, ключ {@code "error"} содержит сообщение об ошибке.</li>
     * </ul>
     */
    @GetMapping("/user/leaders")
    public ResponseEntity<Map<String, Object>> getLeaders(@RequestParam("leadersCount") String leadersCount){
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserWinRate> leadersList = gameService.getLeadersList(leadersCount)
                            .stream()
                                    .map(UserWinRateMapper::fromDomainToWeb)
                                            .toList();
            response.put("leadersList", leadersList);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}