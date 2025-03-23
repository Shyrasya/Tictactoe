package tictactoe.datasource.mapper;

import tictactoe.domain.model.User;

public class UserMapper {

    /**
     * Маппер пользователя из domain в datasource
     *
     * @param domainUser Пользователь из domain (логики)
     * @return Пользователь из datasource
     */
    public static tictactoe.datasource.model.User fromDomainToDataSource(User domainUser) {
        tictactoe.datasource.model.User dataSourceUser = new tictactoe.datasource.model.User();
        dataSourceUser.setUuid(domainUser.getUuid());
        dataSourceUser.setLogin(domainUser.getLogin());
        dataSourceUser.setPassword(domainUser.getPassword());
        dataSourceUser.setRoles(RoleMapper.fromDomainToDataSource(domainUser.getRoles()));
        return dataSourceUser;
    }

    /**
     * Маппер пользователя из datasource в domain
     *
     * @param dataSourceUser Пользователь из datasource
     * @return Пользователь из domain (логики)
     */
    public static User fromDataSourceToDomain(tictactoe.datasource.model.User dataSourceUser) {
        User domainUser = new User();
        domainUser.setUuid(dataSourceUser.getUuid());
        domainUser.setLogin(dataSourceUser.getLogin());
        domainUser.setPassword(dataSourceUser.getPassword());
        domainUser.setRoles(RoleMapper.fromDataSourceToDomain(dataSourceUser.getRoles()));
        return domainUser;
    }
}
