package tictactoe.domain.model;

/**
 * Состояния игры: 0 - ожидание игроков, 1 - игровой процесс, 2 - ничья, 3 - победа
 */
public enum GameState{
    WAIT,
    GAME,
    TIE,
    WIN
}