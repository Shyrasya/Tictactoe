package tictactoe.domain.model;

public class UserStatsDTO {
    /**
     * Всего сыгранных игр (игры в состоянии победы или ничьи)
     */
    private int totalGames;

    /**
     * Процент выигранных игр всего (с компьютером и человеком) от общего количества проведенных игр
     */
    private int percentWinsGames;

    /**
     * Всего игр, проведенных с компьютером
     */
    private int vsComputerGames;

    /**
     * Количество игр, выигранных у компьютера
     */
    private int winsVsComputer;

    /**
     * Процент выигранных игр у компьютера от общего количества игр с компьютером
     */
    private int percentWinsVsComputer;

    /**
     * Количество игр, проигранных компьютеру
     */
    private int lossesVsComputer;

    /**
     * Количество игр-ничьих с компьютером
     */
    private int tiesVsComputer;

    /**
     * Всего игр, проведенных с людьми
     */
    private int vsHumanGames;

    /**
     * Количество игр, выигранных у людей
     */
    private int winsVsHuman;

    /**
     * Процент выигранных игр у людей от общего количества игр с людьми
     */
    private int percentWinsVsHuman;

    /**
     * Количество игр, проигранных людям
     */
    private int lossesVsHuman;

    /**
     * Количество игр-ничьих с людьми
     */
    private int tiesVsHuman;

    /**
     * Базовый конструктор класса UserStatsDTO
     */
    public UserStatsDTO() {}

    /**
     * Параметризированный конструктор класса UserStatsDTO
     * @param totalGames Всего сыгранных игр (игры в состоянии победы или ничьи)
     * @param percentWinsGame Процент выигранных игр всего (с компьютером и человеком) от общего количества проведенных игр
     * @param vsComputerGames Всего игр, проведенных с компьютером
     * @param winsVsComputer Количество игр, выигранных у компьютера
     * @param percentWinsVsComputer Процент выигранных игр у компьютера от общего количества игр с компьютером
     * @param lossesVsComputer Количество игр, проигранных компьютеру
     * @param tiesVsComputer Количество игр-ничьих с компьютером
     * @param vsHumanGames Всего игр, проведенных с людьми
     * @param winsVsHuman Количество игр, выигранных у людей
     * @param percentWinsVsHuman Процент выигранных игр у людей от общего количества игр с людьми
     * @param lossesVsHuman Количество игр, проигранных людям
     * @param tiesVsHuman Количество игр-ничьих с людьми
     */
    public UserStatsDTO(int totalGames, int percentWinsGame, int vsComputerGames, int winsVsComputer, int percentWinsVsComputer,
                        int lossesVsComputer, int tiesVsComputer, int vsHumanGames, int winsVsHuman, int percentWinsVsHuman,
                        int lossesVsHuman, int tiesVsHuman) {
        this.totalGames = totalGames;
        this.percentWinsGames = percentWinsGame;
        this.vsComputerGames = vsComputerGames;
        this.winsVsComputer = winsVsComputer;
        this.percentWinsVsComputer = percentWinsVsComputer;
        this.lossesVsComputer = lossesVsComputer;
        this.tiesVsComputer = tiesVsComputer;
        this.vsHumanGames = vsHumanGames;
        this.percentWinsVsHuman = percentWinsVsHuman;
        this.winsVsHuman = winsVsHuman;
        this.lossesVsHuman = lossesVsHuman;
        this.tiesVsHuman = tiesVsHuman;
    }


    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public int getVsComputerGames() {
        return vsComputerGames;
    }

    public void setVsComputerGames(int vsComputerGames) {
        this.vsComputerGames = vsComputerGames;
    }

    public int getWinsVsComputer() {
        return winsVsComputer;
    }

    public void setWinsVsComputer(int winsVsComputer) {
        this.winsVsComputer = winsVsComputer;
    }

    public int getLossesVsComputer() {
        return lossesVsComputer;
    }

    public void setLossesVsComputer(int lossesVsComputer) {
        this.lossesVsComputer = lossesVsComputer;
    }

    public int getTiesVsComputer() {
        return tiesVsComputer;
    }

    public void setTiesVsComputer(int tiesVsComputer) {
        this.tiesVsComputer = tiesVsComputer;
    }

    public int getVsHumanGames() {
        return vsHumanGames;
    }

    public void setVsHumanGames(int vsHumanGames) {
        this.vsHumanGames = vsHumanGames;
    }

    public int getWinsVsHuman() {
        return winsVsHuman;
    }

    public void setWinsVsHuman(int winsVsHuman) {
        this.winsVsHuman = winsVsHuman;
    }

    public int getLossesVsHuman() {
        return lossesVsHuman;
    }

    public void setLossesVsHuman(int lossesVsHuman) {
        this.lossesVsHuman = lossesVsHuman;
    }

    public int getTiesVsHuman() {
        return tiesVsHuman;
    }

    public void setTiesVsHuman(int tiesVsHuman) {
        this.tiesVsHuman = tiesVsHuman;
    }

    public int getPercentWinsVsComputer() {
        return percentWinsVsComputer;
    }

    public void setPercentWinsVsComputer(int percentWinsVsComputer) {
        this.percentWinsVsComputer = percentWinsVsComputer;
    }

    public int getPercentWinsVsHuman() {
        return percentWinsVsHuman;
    }

    public void setPercentWinsVsHuman(int percentWinsVsHuman) {
        this.percentWinsVsHuman = percentWinsVsHuman;
    }

    public int getPercentWinsGames() {
        return percentWinsGames;
    }

    public void setPercentWinsGames(int percentWinsGames) {
        this.percentWinsGames = percentWinsGames;
    }
}
