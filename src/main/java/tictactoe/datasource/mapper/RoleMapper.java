package tictactoe.datasource.mapper;

import tictactoe.datasource.model.Role;

import java.util.Set;
import java.util.stream.Collectors;

public class RoleMapper {

    /**
     * Маппер роли пользователя из domain в datasource
     *
     * @param domainRoles Роли пользователя из domain (логики)
     * @return Роли пользователя из datasource
     */
    public static Set<Role> fromDomainToDataSource(Set<tictactoe.domain.model.Role> domainRoles) {
        return domainRoles.stream()
                .map(role -> Role.valueOf(role.name()))
                .collect(Collectors.toSet());
    }

    /**
     * Маппер роли пользователя из datasource в domain
     *
     * @param dataSourceRoles Роли пользователя из datasource
     * @return Роли пользователя из domain (логики)
     */
    public static Set<tictactoe.domain.model.Role> fromDataSourceToDomain(Set<Role> dataSourceRoles) {
        return dataSourceRoles.stream()
                .map(role -> tictactoe.domain.model.Role.valueOf(role.name()))
                .collect(Collectors.toSet());
    }
}
