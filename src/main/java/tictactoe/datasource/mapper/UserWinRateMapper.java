package tictactoe.datasource.mapper;

import tictactoe.domain.model.UserWinRate;

public class UserWinRateMapper {
    /**
     * Маппер UserWinRate из domain в datasource
     *
     * @param domainUserWinRate UserWinRate из domain (логики)
     * @return UserWinRate из datasource
     */
    public static tictactoe.datasource.model.UserWinRate fromDomainToDataSource(UserWinRate domainUserWinRate) {
        tictactoe.datasource.model.UserWinRate dataSourceUserWinRate = new tictactoe.datasource.model.UserWinRate();
        dataSourceUserWinRate.setUserUuid(domainUserWinRate.getUserUuid());
        dataSourceUserWinRate.setWinRate(domainUserWinRate.getWinRate());
        dataSourceUserWinRate.setLogin(domainUserWinRate.getLogin());
        return dataSourceUserWinRate;
    }

    /**
     * Маппер UserWinRate из datasource в domain
     *
     * @param dataSourceUserWinRate UserWinRate из datasource
     * @return UserWinRate из domain (логики)
     */
    public static UserWinRate fromDataSourceToDomain(tictactoe.datasource.model.UserWinRate dataSourceUserWinRate) {
        UserWinRate domainUserWinRate = new UserWinRate();
        domainUserWinRate.setUserUuid(dataSourceUserWinRate.getUserUuid());
        domainUserWinRate.setWinRate(dataSourceUserWinRate.getWinRate());
        domainUserWinRate.setLogin(dataSourceUserWinRate.getLogin());
        return domainUserWinRate;
    }
}
