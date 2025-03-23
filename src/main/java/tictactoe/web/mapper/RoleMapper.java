package tictactoe.web.mapper;

import tictactoe.web.model.Role;

public class RoleMapper {

    /**
     * Маппер роли пользователя из domain в web
     *
     * @param domainRole Роль пользователя из domain (логики)
     * @return Роль пользователя из web
     */
    public static Role fromDomainToWeb(tictactoe.domain.model.Role domainRole) {
        return Role.valueOf(domainRole.name());
    }

    /**
     * Маппер роли пользователя из web в domain
     *
     * @param webRole Роль пользователя из web
     * @return domain Роль пользователя из domain (логики)
     */
    public static tictactoe.domain.model.Role fromWebToDomain(Role webRole) {
        return tictactoe.domain.model.Role.valueOf(webRole.name());
    }
}
