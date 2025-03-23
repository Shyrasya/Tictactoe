package tictactoe.domain.model;

public class ScoreMove {
    /**
     * Результат минимакса
     */
    private int bestScore;

    /**
     * Позиция клетки, где стоит символ хода сервера
     */
    private int[] bestPosition;

    /**
     * Параметризированный конструктор класса ScoreMove
     *
     * @param bestScore    Результат минимакса
     * @param bestPosition Позиция клетки, где стоит символ хода сервера
     */
    public ScoreMove(int bestScore, int[] bestPosition) {
        this.bestScore = bestScore;
        this.bestPosition = bestPosition;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int[] getBestPosition() {
        return bestPosition;
    }

    public void setBestPosition(int[] bestPosition) {
        this.bestPosition = bestPosition;
    }
}
