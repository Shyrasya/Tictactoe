package tictactoe.web.mapper;

import tictactoe.web.model.RefreshJwtRequest;

public class RefreshJwtRequestMapper {
    /**
     * Маппер обновления Jwt-запроса из domain в web
     *
     * @param domainRefreshJwtRequest Обновление Jwt-запроса из domain
     * @return Обновление Jwt-запроса из web
     */
    public static RefreshJwtRequest fromDomainToWeb(tictactoe.domain.model.RefreshJwtRequest domainRefreshJwtRequest) {
        RefreshJwtRequest webJwtRequest = new RefreshJwtRequest();
        webJwtRequest.setRefreshToken(domainRefreshJwtRequest.getRefreshToken());
        return webJwtRequest;
    }

    /**
     * Маппер обновления Jwt-запроса из domain в web
     *
     * @param webRefreshJwtRequest Обновление Jwt-запроса из domain
     * @return Обновление Jwt-запроса из web
     */
    public static tictactoe.domain.model.RefreshJwtRequest fromWebToDomain(RefreshJwtRequest webRefreshJwtRequest) {
        tictactoe.domain.model.RefreshJwtRequest domainRefreshJwtRequest = new tictactoe.domain.model.RefreshJwtRequest();
        domainRefreshJwtRequest.setRefreshToken(webRefreshJwtRequest.getRefreshToken());
        return domainRefreshJwtRequest;
    }
}
