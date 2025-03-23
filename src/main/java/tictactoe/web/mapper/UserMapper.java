package tictactoe.web.mapper;

import tictactoe.web.model.User;

public class UserMapper {

    /**
     * Маппер пользователя из domain в web
     *
     * @param domainUser Пользователь из domain
     * @return Пользователь из web
     */
    public static User fromDomainToWeb(tictactoe.domain.model.User domainUser) {
        User webUser = new User();
        webUser.setUuid(domainUser.getUuid());
        webUser.setLogin(domainUser.getLogin());
        webUser.setPassword(domainUser.getPassword());
        webUser.setRoles(domainUser.getRoles());
        return webUser;
    }

    /**
     * Маппер игрового поля из domain в web
     *
     * @param webUser Игровое поле из domain
     * @return Игровое поле из web
     */
    public static tictactoe.domain.model.User fromWebToDomain(User webUser) {
        tictactoe.domain.model.User domainUser = new tictactoe.domain.model.User();
        domainUser.setUuid(webUser.getUuid());
        domainUser.setLogin(webUser.getLogin());
        domainUser.setPassword(webUser.getPassword());
        domainUser.setRoles(webUser.getRoles());
        return domainUser;
    }
}
