package tictactoe.datasource.model;

import java.util.UUID;

public class UserWinRate {
    /**
     * Логин пользователя
     */
    private String login;

    /**
     * Уникальный идентификатор пользователя
     */
    private UUID userUuid;

    /**
     * Соотношение побед к ничьим и поражениям
     */
    private Double winRate;

    /**
     * Базовый конструктор класса UserWinRate
     */
    public UserWinRate() {
    }

    /**
     * Параметризированный конструктор класса UserWinRate
     *
     * @param login Логин пользователя
     * @param userUuid Уникальный идентификатор пользователя
     * @param winRate Соотношение побед к ничьим и поражениям
     */
    public UserWinRate(String login, UUID userUuid, Double winRate) {
        this.login = login;
        this.userUuid = userUuid;
        this.winRate = (winRate == null) ? 0.0 : winRate;;
    }


    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
