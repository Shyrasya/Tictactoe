package tictactoe.web.mapper;

import tictactoe.web.model.UserWinRate;

public class UserWinRateMapper {
    /**
     * Маппер UserWinRate из domain в web
     *
     * @param domainUserWinRate UserWinRate из domain
     * @return UserWinRate из web
     */
    public static UserWinRate fromDomainToWeb(tictactoe.domain.model.UserWinRate domainUserWinRate) {
        UserWinRate webUserWinRate = new UserWinRate();
        webUserWinRate.setUserUuid(domainUserWinRate.getUserUuid());
        webUserWinRate.setWinRate(domainUserWinRate.getWinRate());
        webUserWinRate.setLogin(domainUserWinRate.getLogin());
        return webUserWinRate;
    }

    /**
     * Маппер UserWinRate из domain в web
     *
     * @param webUserWinRate UserWinRate из domain
     * @return UserWinRate из web
     */
    public static tictactoe.domain.model.UserWinRate fromWebToDomain(UserWinRate webUserWinRate) {
        tictactoe.domain.model.UserWinRate domainUserWinRate = new tictactoe.domain.model.UserWinRate();
        domainUserWinRate.setUserUuid(webUserWinRate.getUserUuid());
        domainUserWinRate.setWinRate(webUserWinRate.getWinRate());
        domainUserWinRate.setLogin(webUserWinRate.getLogin());
        return domainUserWinRate;
    }
}
