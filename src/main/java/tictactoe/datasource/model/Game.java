package tictactoe.datasource.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "game_storage")
public class Game {
    /**
     * Уникальный идентификатор игры
     */
    @Id
    @Column(columnDefinition = "UUID", nullable = false)
    private UUID uuid;

    /**
     * Игровое поле
     */
    @Embedded
    private GameBoard board;

    /**
     * Дата и время создания игры
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime gameCreationDate;

    /**
     * Текущее состоянии игры
     */
    @Column(nullable = false)
    private GameState state;

    /**
     * Уникальный идентификатор первого игрока
     */
    @Column(nullable = false)
    private UUID playerOneUuid;

    /**
     * Уникальный идентификатор второго игрока
     */
    private UUID playerTwoUuid;

    /**
     * Уникальный идентификатор игрока, чей ход текущий
     */
    private UUID currentPlayerUuid;

    /**
     * Уникальный идентификатор игрока-победителя
     */
    private UUID winnerUuid;

    /**
     * Чей сейчас ход: 1 - крестика, 2 - нолика
     */
    @Column(nullable = false)
    private int nowTurnSign;
    /**
     * Текущий игрок, знак (1 - крестик, 2 - нолик)
     */
    @Column(nullable = false)
    private int playerOneSign;

    /**
     * Противоположный игрок, знак (1 - крестик, 2 нолик)
     */
    @Column(nullable = false)
    private int playerTwoSign;

    /**
     * Базовый конструктор класса Game
     */
    public Game() {
        this.board = new GameBoard();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public GameBoard getBoard() {
        return board;
    }

    public void setBoard(GameBoard board) {
        this.board = board;
    }

    /**
     * Получение информации о том, чей сейчас ход
     *
     * @return 1 - ход крестика, 2 - нолика
     */
    public int getNowTurnSign() {
        return nowTurnSign;
    }

    /**
     * Установка информации о том, чей сейчас ход
     *
     * @param nowTurnSign 1 - ход крестика, 2 - нолика
     */
    public void setNowTurnSign(int nowTurnSign) {
        this.nowTurnSign = nowTurnSign;
    }

    /**
     * Получение игрового знака текущего игрока
     *
     * @return 1 - крестик, 2 - нолик
     */
    public int getPlayerOneSign() {
        return playerOneSign;
    }

    /**
     * Установка игрового значка текущему игроку
     *
     * @param playerOneSign Текущий игрок (1 - крестик, 2 - нолик)
     */
    public void setPlayerOneSign(int playerOneSign) {
        this.playerOneSign = playerOneSign;
    }

    /**
     * Получение игрового знака игрока-противника
     *
     * @return 1 - крестик, 2 - нолик
     */
    public int getPlayerTwoSign() {
        return playerTwoSign;
    }

    /**
     * Установка игрового значка игроку-противнику
     *
     * @param playerTwoSign Игрок-противник (1 - крестик, 2 - нолик)
     */
    public void setPlayerTwoSign(int playerTwoSign) {
        this.playerTwoSign = playerTwoSign;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public UUID getCurrentPlayerUuid() {
        return currentPlayerUuid;
    }

    public void setCurrentPlayerUuid(UUID currentPlayerUuid) {
        this.currentPlayerUuid = currentPlayerUuid;
    }

    public UUID getWinnerUuid() {
        return winnerUuid;
    }

    public void setWinnerUuid(UUID winnerUuid) {
        this.winnerUuid = winnerUuid;
    }

    public UUID getPlayerOneUuid() {
        return playerOneUuid;
    }

    public void setPlayerOneUuid(UUID playerOneUuid) {
        this.playerOneUuid = playerOneUuid;
    }

    public UUID getPlayerTwoUuid() {
        return playerTwoUuid;
    }

    public void setPlayerTwoUuid(UUID playerTwoUuid) {
        this.playerTwoUuid = playerTwoUuid;
    }

    public LocalDateTime getGameCreationDate() {
        return gameCreationDate;
    }

    public void setGameCreationDate(LocalDateTime gameCreationDate) {
        this.gameCreationDate = gameCreationDate;
    }
}
