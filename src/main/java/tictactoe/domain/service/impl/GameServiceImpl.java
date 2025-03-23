package tictactoe.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import tictactoe.datasource.mapper.GameMapper;
import tictactoe.datasource.mapper.GameStateMapper;
import tictactoe.datasource.mapper.UserWinRateMapper;
import tictactoe.datasource.repository.GameRepository;
import tictactoe.domain.model.*;
import tictactoe.domain.service.GameService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GameServiceImpl implements GameService {

    /**
     * Репозиторий для работы с базой данных игр game_storage
     */
    private final GameRepository gameRepository;

    private final static int MAX_SCORE = 10;
    private final static int INIT = -1;
    private final static int LINE_SIZE = 3;
    public static final int EMPTY_CELL = 0;

    private final static int CROSS = 1;
    private final static int CIRCLE = 2;

    /**
     * Параметризированный конструктор класса GameServiceImpl
     *
     * @param gameRepository Репозиторий для работы с базой данных игр game_storage
     */
    @Autowired
    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Получает логин пользователя по его Uuid.
     *
     * @return Логин пользователя
     */
    @Override
    public String getUserLogin(){
        UUID userUuid = getUserUuid();
        return gameRepository.getLoginByUuidUser(userUuid);
    }

    /**
     * Получает UUID пользователя из текущего контекста безопасности.
     *
     * @return UUID пользователя, извлечённый из текущей аутентификации.
     */
    @Override
    public UUID getUserUuid(){
        Authentication jwtAuth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(jwtAuth.getName());
    }

    /**
     * Получение игры из базы данных игр game_storage по уникальному идентификатору
     *
     * @param uuid Уникальный идентификатор игры
     * @return Игра из базы данных игр game_storage
     */
    @Override
    public Game getGame(UUID uuid) {
        return gameRepository.findById(uuid)
                .map(GameMapper::fromDataSourceToDomain)
                .orElse(null);
    }

    /**
     * Получение UUID игры, находящейся в статусе ожидание или игровой процесс, по UUID игрока
     *
     * @param userUuid Уникальный идентификатор пользователя
     * @return Активная игра с искомым пользователем
     */
    @Override
    public UUID activeGame(UUID userUuid) {
        List<tictactoe.datasource.model.GameState> activeStates = Arrays.asList(
                GameStateMapper.fromDomainToDataSource(GameState.WAIT),
                GameStateMapper.fromDomainToDataSource(GameState.GAME)
        );
        List<tictactoe.datasource.model.Game> activeGame = gameRepository.findGamesByPlayerUuidAndStateIn(userUuid, activeStates);
        return activeGame.isEmpty() ? null : activeGame.get(0).getUuid();
    }

    /**
     * Пустой ли список игр, находящихся в статусе ожидание и имеющих знак, противоположный выбранному
     *
     * @param playerChoice Выбранный игроком, делющим запрос, игровой знак
     * @return true - список пуст, false - нет
     */
    @Override
    public boolean noNewGames(String playerChoice) {
        List<UUID> newGames = waitGames(playerChoice);
        return newGames.isEmpty();
    }

    /**
     * Получение списка UUID игр, которые находятся в статусе ожидание и имеющих знак, противоположный выбранному
     *
     * @param playerChoice Выбранный игроком, делющим запрос, игровой знак
     * @return Список UUID найденных игр
     */
    @Override
    public List<UUID> waitGames(String playerChoice) {
        int opponentSign = playerChoice.equals("X") ? CIRCLE : CROSS;
        return gameRepository.findByPlayerOneSignAndState(opponentSign, GameStateMapper.fromDomainToDataSource(GameState.WAIT));
    }

    /**
     * Проверка игры на возможность начала вторым игроком
     *
     * @param gameUuid         UUID проверяемой игры
     * @param secondPlayerUuid UUID подсоединяющегося к игре игрока
     * @return true - игра доступна для начала, false - нет
     */
    @Override
    @Transactional
    public boolean prepareGameForSecondPlayer(String gameUuid, UUID secondPlayerUuid) {
        boolean isFreeGame = true;
        Game findGame = getGame(UUID.fromString(gameUuid));
        if (findGame == null || findGame.getState() != GameState.WAIT)
            isFreeGame = false;
        else {
            findGame.setState(GameState.GAME);
            findGame.setPlayerTwoUuid(secondPlayerUuid);
            updateGame(findGame);
        }
        return isFreeGame;
    }

    /**
     * Создание новой игры в базе данных игр game_storage
     *
     * @param playerChoice  Выбор игрового знака игроком (1 - крестик, 2 - нолик)
     * @param playerOneUuid Уникальный идентификатор первого игрока
     * @param state         Состояние новой игры
     * @return Новая игра
     */
    @Override
    @Transactional
    public Game initServerGame(String playerChoice, UUID playerOneUuid, GameState state) {
        Game newGame = new Game();
        newGame.setUuid(UUID.randomUUID());
        newGame.setState(state);
        newGame.setPlayerOneUuid(playerOneUuid);
        newGame.setCurrentPlayerUuid(playerOneUuid);
        int playerChoiceInt = playerChoice.equals("X") ? CROSS : CIRCLE;
        newGame.setNowTurnSign(playerChoiceInt);
        newGame.setPlayerOneSign(playerChoiceInt);
        newGame.setPlayerTwoSign(playerChoiceInt == CROSS ? CIRCLE : CROSS);
        newGame.setGameCreationDate(LocalDateTime.now());
        updateGame(newGame);
        return newGame;
    }

    /**
     * Обновление игры в базе данных игр game_storage
     *
     * @param clientGame Обновленная игра
     */
    @Override
    @Transactional
    public void updateGame(Game clientGame) {
        gameRepository.save(GameMapper.fromDomainToDataSource(clientGame));
    }

    /**
     * Проверка статуса игры в зависимости от расстановки ходов на поле и ход сервера при продолжении игры
     *
     * @param dataGame       Игровое поле без хода сервера
     * @param cellIndex      Индекс кнопки, которую выбрал клиент в качестве собственного хода
     * @param movePlayerUuid UUID пользователя, делающего ход (не обязательно его ход сейчас)
     * @return Объект класса MoveInfo, содержащий обновленное игровое поле и сопутсвующее сообщение о состоянии игры
     */
    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<MoveInfo> checkMove(Game dataGame, int cellIndex, UUID movePlayerUuid) {
        synchronized (dataGame) {
            MoveInfo moveInfo = new MoveInfo();
            if (isWaitingOpponent(dataGame, movePlayerUuid)) {
                moveInfo.setMessage("Ожидание противника!");
                moveInfo.setGame(dataGame);
                return CompletableFuture.completedFuture(moveInfo);
            }
            int moveRow = cellIndex / LINE_SIZE;
            int moveColumn = cellIndex % LINE_SIZE;

            if (dataGame.getState() == GameState.WIN || dataGame.getState() == GameState.TIE) {
                moveInfo.setMessage("Игра уже окончена! Начните новую игру!");
                moveInfo.setGame(dataGame);
            } else if (!boardValidation(dataGame.getBoard(), moveRow, moveColumn)) {
                moveInfo.setMessage("Неправильный ход!");
                moveInfo.setGame(dataGame);
            } else {
                dataGame.getBoard().setElement(moveRow, moveColumn, dataGame.getNowTurnSign());
                dataGame.setNowTurnSign(dataGame.getNowTurnSign() == CROSS ? CIRCLE : CROSS);

                if (isWin(dataGame.getBoard())) {
                    dataGame.setState(GameState.WIN);
                    dataGame.setWinnerUuid(dataGame.getCurrentPlayerUuid());
                    moveInfo.setMessage("Победили " + getWinnerName(dataGame));
                    moveInfo.setGame(dataGame);
                } else if (isTie(dataGame.getBoard())) {
                    dataGame.setState(GameState.TIE);
                    moveInfo.setMessage("Ничья!");
                    moveInfo.setGame(dataGame);
                } else if (dataGame.getState() == GameState.GAME && dataGame.getPlayerTwoUuid() == null) {
                    moveInfo = setMoveInfo(dataGame);
                    moveInfo.setMessage(moveInfo.getMessage());
                } else if (dataGame.getPlayerTwoUuid() != null) {
                    moveInfo.setGame(dataGame);
                    UUID nextTurnUuid = dataGame.getCurrentPlayerUuid().equals(dataGame.getPlayerOneUuid()) ? dataGame.getPlayerTwoUuid() : dataGame.getPlayerOneUuid();
                    dataGame.setCurrentPlayerUuid(nextTurnUuid);
                }
                updateGame(dataGame);
            }
            return CompletableFuture.completedFuture(moveInfo);
        }
    }

    /**
     * Находится ли в статусе ожидания игрок, который сделал ход
     *
     * @param dataGame       Текущая игра
     * @param movePlayerUuid UUID игрока, кто сделал ход
     * @return true- да, игрок в статусе ожидания, false - нет
     */
    private boolean isWaitingOpponent(Game dataGame, UUID movePlayerUuid) {
        GameState state = dataGame.getState();
        return (state == GameState.WAIT || (state.equals(GameState.GAME) && !dataGame.getCurrentPlayerUuid().equals(movePlayerUuid)));
    }

    /**
     * Установка шага сервера на карту на основе алгоритма минимакс с последующим обновлением карты в базе данных игр game_storage
     *
     * @param dataGame Игровое поле без хода сервера
     * @return Объект класса MoveInfo, содержащий обновленное игровое поле и сопутсвующее сообщение о состоянии игры
     */
    public MoveInfo setMoveInfo(Game dataGame) {
        String message = "";
        int[] moveInfoPosition = getNextServerTurn(dataGame);
        dataGame.getBoard().setElement(moveInfoPosition[0], moveInfoPosition[1], dataGame.getNowTurnSign());
        if (isWin(dataGame.getBoard())) {
            dataGame.setState(GameState.WIN);
            message = "Победили " + getWinnerName(dataGame);
        }
        dataGame.setNowTurnSign(dataGame.getPlayerOneSign());
        return new MoveInfo(message, dataGame);
    }

    /**
     * Метод получения следующего хода текущей игры с помощью алгоритма "Минимакс"
     *
     * @param game Текущая игра с прошлым ходом клиента
     * @return Координаты с лучшим следующим ходом
     */
    public int[] getNextServerTurn(Game game) {
        GameBoard board = game.getBoard();
        int[] bestPosition = {INIT, INIT};
        int bestScore = Integer.MIN_VALUE;
        for (int row = 0; row < GameBoard.LINE_SIZE && bestScore != MAX_SCORE; row++) {
            for (int column = 0; column < GameBoard.LINE_SIZE && bestScore != MAX_SCORE; column++) {
                if (board.getElement(row, column) == GameBoard.EMPTY_CELL) {
                    if (boardValidation(board, row, column)) {
                        ScoreMove scoreMove = checkCellByServer(row, column, bestScore, bestPosition, game);
                        bestScore = scoreMove.getBestScore();
                        bestPosition = scoreMove.getBestPosition();
                    } else
                        throw new IllegalStateException("Игровое поле было изменено сервером!");
                }
            }
        }
        return bestPosition;
    }

    /**
     * Проверка текущей клетки на следующий выигрышный ход от клиента и на результат минимакса
     *
     * @param row          Строка игрового поля, на которой проверяем ход
     * @param column       Столбец игрового поля, на котором проверяем ход
     * @param bestScore    Лучший результат из всех проверок
     * @param bestPosition Координата с лучшим ходом сервера
     * @param game         Текущая игра
     * @return Обновленный лучший результат и координата хода вервера
     */
    private ScoreMove checkCellByServer(int row, int column, int bestScore, int[] bestPosition, Game game) {
        int depthMinimax = 0;
        boolean isMaximizationMove = false;
        GameBoard board = game.getBoard();

        board.setElement(row, column, game.getPlayerOneSign());
        if (isWin(board)) {
            bestScore = MAX_SCORE;
            bestPosition[0] = row;
            bestPosition[1] = column;
        }

        board.setElement(row, column, game.getPlayerTwoSign());
        int scoreMove = minimax(game, depthMinimax, isMaximizationMove);
        if (scoreMove > bestScore) {
            bestScore = scoreMove;
            bestPosition[0] = row;
            bestPosition[1] = column;
        }

        board.setElement(row, column, GameBoard.EMPTY_CELL);
        return new ScoreMove(bestScore, bestPosition);
    }

    /**
     * Алгоритм для поиска числа, обозначающего исход игры при данной расстановке символов на карту
     *
     * @param game               Текущая игра
     * @param depthMinimax       Глубина рекурсии поиска минимакса
     * @param isMaximizationMove Сейчас ход стороны, максимизирующего свою победу? (true - да, false - нет)
     * @return Число исхода игры (положительное - выигрыш текущего игра,  отрицательное - выигрыш оппонента, 0 - ничья)
     */
    public int minimax(Game game, int depthMinimax, boolean isMaximizationMove) {
        GameBoard board = game.getBoard();
        int scoreBest;
        if (isGameOver(board))
            scoreBest = countScore(board, depthMinimax);
        else
            scoreBest = minimaxMove(game, depthMinimax, isMaximizationMove);
        return scoreBest;
    }

    /**
     * Метод продолжения глубины минимакса и выбор поведения при статусе хода
     *
     * @param game               Текущая игра
     * @param depthMinimax       Глубина рекурсии поиска минимакса
     * @param isMaximizationMove Статус текущего хода (максиммизирующий или минимизирующий)
     * @return Число исхода игры (положительное - выигрыш текущего игра,  отрицательное - выигрыш оппонента, 0 - ничья)
     */
    private int minimaxMove(Game game, int depthMinimax, boolean isMaximizationMove) {
        GameBoard board = game.getBoard();
        int scoreBest;
        if (isMaximizationMove) scoreBest = Integer.MIN_VALUE;
        else scoreBest = Integer.MAX_VALUE;
        for (int row = 0; row < GameBoard.LINE_SIZE; row++) {
            for (int column = 0; column < GameBoard.LINE_SIZE; column++) {
                if (board.getElement(row, column) == GameBoard.EMPTY_CELL) {
                    if (boardValidation(board, row, column)) {
                        int nowMoveTurn = isMaximizationMove ? game.getPlayerTwoSign() : game.getPlayerOneSign();
                        board.setElement(row, column, nowMoveTurn);
                        boolean maxMinNext = !isMaximizationMove;
                        int scoreMove = minimax(game, depthMinimax + 1, maxMinNext);
                        board.setElement(row, column, GameBoard.EMPTY_CELL);
                        scoreBest = isMaximizationMove ? Math.max(scoreMove, scoreBest) : Math.min(scoreMove, scoreBest);
                    } else
                        throw new IllegalStateException("Игровое поле было изменено сервером!");
                }
            }
        }
        return scoreBest;
    }


    /**
     * Проверка на сохранность поля(прошлых ходов) после текущего хода
     *
     * @param boardOld   Поле до последнего хода
     * @param moveRow    Строка, на которой находится ход игрока
     * @param moveColumn Столбец, на котором находится ход игрока
     * @return true - поле в порядке, ничего не нарушено, false - есть нарушения порядка
     */
    @Override
    public boolean boardValidation(GameBoard boardOld, int moveRow, int moveColumn) {
        return boardOld.getElement(moveRow, moveColumn) == EMPTY_CELL;
    }

    /**
     * Возвращает оценку окончания игры для текущего игрока
     *
     * @param board        Поле
     * @param depthMinimax Глубина рекурсии поиска минимакса
     * @return Числовая оценка (положительная оценка - свой выигрыш, отрицательная оценка - выигрыш оппонента, ничья - 0
     */
    public int countScore(GameBoard board, int depthMinimax) {
        int score = -MAX_SCORE + depthMinimax;
        if (isWin(board))
            score = MAX_SCORE - depthMinimax;
        else if (isTie(board))
            score = 0;
        return score;
    }

    /**
     * Метод для проверки окончания игры
     *
     * @param board Поле
     * @return true - игра окончена, false - игра продолжается
     */
    @Override
    public boolean isGameOver(GameBoard board) {
        return isWin(board) || isTie(board);
    }

    /**
     * Проверка поля на победу (закрашено необходимое количество клеток одним символом?)
     *
     * @param board Поле
     * @return true - победа, false - нет
     */
    @Override
    public boolean isWin(GameBoard board) {
        int[][] checkBoard = board.getBoard();
        boolean win = false;
        for (int row = 0; row < GameBoard.LINE_SIZE && !win; row++) {
            if (checkBoard[row][0] == checkBoard[row][1] && checkBoard[row][1] == checkBoard[row][2] && checkBoard[row][2] != GameBoard.EMPTY_CELL)
                win = true;
        }
        for (int column = 0; column < GameBoard.LINE_SIZE && !win; column++) {
            if (checkBoard[0][column] == checkBoard[1][column] && checkBoard[1][column] == checkBoard[2][column] && checkBoard[2][column] != GameBoard.EMPTY_CELL)
                win = true;
        }
        if (!win) {
            int center = checkBoard[1][1];
            if (checkBoard[0][0] == center && center == checkBoard[2][2] && checkBoard[2][2] != GameBoard.EMPTY_CELL)
                win = true;
            else if (checkBoard[0][2] == center && center == checkBoard[2][0] && checkBoard[2][0] != GameBoard.EMPTY_CELL)
                win = true;
        }
        return win;
    }

    /**
     * Метод для определения ничьи по поиску свободных клеток на поле (со значением 0)
     *
     * @param board Поле
     * @return true - свободных клеток нет (ничья), false - место для хода еще есть
     */
    @Override
    public boolean isTie(GameBoard board) {
        boolean tie = true;
        for (int row = 0; row < GameBoard.LINE_SIZE && tie; row++) {
            for (int column = 0; column < GameBoard.LINE_SIZE && tie; column++) {
                if (board.getElement(row, column) == GameBoard.EMPTY_CELL)
                    tie = false;
            }
        }
        return tie;
    }

    /**
     * Получение знака-победителя
     *
     * @param dataGame Текущая игра
     * @return Строка с победителем (крестики/нолики)
     */
    @Override
    public String getWinnerName(Game dataGame) {
        UUID winnerUuid = dataGame.getWinnerUuid();
        int winnerIntSign;
        if (winnerUuid != null) {
            String winnerUser = winnerUuid.equals(dataGame.getPlayerOneUuid()) ? "playerOne" : "playerTwo";
            winnerIntSign = winnerUser.equals("playerOne") ? dataGame.getPlayerOneSign() : dataGame.getPlayerTwoSign();
        } else
            winnerIntSign = dataGame.getPlayerTwoSign();
        return winnerIntSign == CROSS ? "Крестики!" : "Нолики!";
    }

    /**
     * Метод удаления игры из базы данных game_storage
     *
     * @param gameUuid UUID удаляемой игры
     */
    @Override
    @Transactional
    public void deleteGame(UUID gameUuid) {
        gameRepository.deleteById(gameUuid);
    }

    /**
     * Метод получения игровой статистики по завершенным играм по UUID пользователя из базы данных game_storage
     *
     * @param userUuid UUID игрока
     * @return Объект класса UserStatsDTO, содержащий статистику по завершенным играм выбранного игрока
     */
    @Override
    public UserStatsDTO getUsersStatsByUuid(UUID userUuid) {
        List<tictactoe.datasource.model.GameState> endGameStates = Arrays.asList(
                GameStateMapper.fromDomainToDataSource(GameState.TIE),
                GameStateMapper.fromDomainToDataSource(GameState.WIN));
        List<Game> filterGames = Optional.ofNullable(
                        gameRepository.findGamesByPlayerUuidAndStateIn(userUuid, endGameStates))
                .orElse(Collections.emptyList())
                .stream()
                .map(GameMapper::fromDataSourceToDomain)
                .toList();

        int totalGames = filterGames.size();
        Map<Boolean, List<Game>> partitionedGames = filterGames.stream()
                .collect(Collectors.partitioningBy(g -> g.getPlayerTwoUuid() == null));
        List<Game> computerGames = partitionedGames.get(true);
        List<Game> humanGames = partitionedGames.get(false);
        int vsComputerGames = computerGames.size();
        int vsHumanGames = humanGames.size();

        Map<Boolean, List<Game>> winsComputer = computerGames.stream()
                .collect(Collectors.partitioningBy(g -> g.getState() == GameState.WIN && Objects.equals(g.getWinnerUuid(), userUuid)));
        int winsVsComputer = winsComputer.getOrDefault(true, Collections.emptyList()).size();
        int lossesVsComputer = winsComputer.getOrDefault(false, Collections.emptyList()).stream()
                .filter(g -> g.getState() == GameState.WIN).toList().size();
        int tiesVsComputer = vsComputerGames - winsVsComputer - lossesVsComputer;

        Map<Boolean, List<Game>> winsHuman = humanGames.stream()
                .collect(Collectors.partitioningBy(g -> g.getState() == GameState.WIN && Objects.equals(g.getWinnerUuid(), userUuid)));
        int winsVsHuman = winsHuman.getOrDefault(true, Collections.emptyList()).size();
        int lossesVsHuman = winsHuman.getOrDefault(false, Collections.emptyList()).stream()
                .filter(g -> g.getState() == GameState.WIN).toList().size();
        int tiesVsHuman = vsHumanGames - winsVsHuman - lossesVsHuman;
        int percentWinsGame = totalGames > 0 ? (winsVsComputer + winsVsHuman) * 100 / totalGames : 0;
        int percentWinsVsComputer = vsComputerGames > 0 ? winsVsComputer * 100 / vsComputerGames : 0;
        int percentWinsVsHuman = vsHumanGames > 0 ? winsVsHuman * 100 / vsHumanGames : 0;
        return new UserStatsDTO(totalGames, percentWinsGame, vsComputerGames, winsVsComputer, percentWinsVsComputer, lossesVsComputer, tiesVsComputer,
                vsHumanGames, winsVsHuman, percentWinsVsHuman, lossesVsHuman, tiesVsHuman);
    }

    /**
     * Получает список завершённых игр (ничьи или победы) для текущего пользователя.
     *
     * @return Список объектов {@link Game}, представляющих завершённые игры текущего пользователя.
     */
    @Override
    public List<Game> getUserEndGames(){
        UUID userUuid = getUserUuid();
        return gameRepository.findEndGamesByPlayerUuid(userUuid)
                .stream()
                .map(GameMapper::fromDataSourceToDomain)
                .toList();
    }

    /**
     * Получает список лучших игроков с их соотношением побед.
     *
     * @param leadersCount Строковое значение, представляющее количество лучших игроков, которые должны быть получены.
     * @return Список объектов {@link UserWinRate}, представляющих лучших игроков и их соотношение побед.
     */
    @Override
    public List<UserWinRate> getLeadersList(String leadersCount){
        int allUserCount = gameRepository.countTotalUsers();
        int leadersCountInt = Integer.parseInt(leadersCount);
        if (allUserCount < leadersCountInt)
            leadersCountInt = allUserCount;
        Pageable pageable = PageRequest.of(0, leadersCountInt);
        return gameRepository.getLeadersList(pageable)
                .stream()
                .map(UserWinRateMapper::fromDataSourceToDomain)
                .toList();
    }


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
    @Override
    public Map<String, String> getPlayerChoiceByUserUuid(UUID userUuid, UUID gameUuid){
        Map<String, String> lastActiveGameChoice = new HashMap<>();
        Game game = getGame(gameUuid);
        if (game.getPlayerOneUuid().equals(userUuid))
            lastActiveGameChoice.put("playerChoice", game.getPlayerOneSign() == CROSS ? "X" : "O");
        else
            lastActiveGameChoice.put("playerChoice", game.getPlayerTwoSign() == CROSS ? "X" : "O");
        if (!game.getState().equals(GameState.WAIT) && game.getPlayerTwoUuid() == null)
            lastActiveGameChoice.put("opponent", "computer");
        else
            lastActiveGameChoice.put("opponent", "human");
        return lastActiveGameChoice;
    }
}
