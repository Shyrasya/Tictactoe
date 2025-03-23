package tictactoe.web.model;

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
