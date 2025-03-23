package tictactoe.datasource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

@Embeddable
public class GameBoard {
    /**
     * Игровое поле 3*3
     */
    @Convert(converter = GameBoardConverter.class)
    @Column(columnDefinition = "TEXT")
    private int[][] board;

    /**
     * Количество клеток в ряд на поле
     */
    public static final int LINE_SIZE = 3;

    /**
     * Базовый конструктор класса GameBoard
     */
    public GameBoard() {
        this.board = new int[LINE_SIZE][LINE_SIZE];
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    /**
     * Взять содержимое игрового поля по координатам клетки
     *
     * @param row    Строка
     * @param column Столбец
     * @return Содержимое клетки (0 - пустая, 1- крестик, 2 - нолик)
     */
    public int getElement(int row, int column) {
        int element = -1;
        if (row < LINE_SIZE && column < LINE_SIZE && row >= 0 && column >= 0) {
            element = board[row][column];
        }
        return element;
    }

    /**
     * Установить значение в клетку по координатам
     *
     * @param row     Строка
     * @param column  Столбец
     * @param element Значение (0 - пустая, 1- крестик, 2 - нолик)
     */
    public void setElement(int row, int column, int element) {
        board[row][column] = element;
    }
}
