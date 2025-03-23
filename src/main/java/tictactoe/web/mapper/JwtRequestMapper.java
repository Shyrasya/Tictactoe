package tictactoe.web.mapper;

import tictactoe.web.model.JwtRequest;

public class JwtRequestMapper {
    /**
     * Маппер Jwt-запроса из domain в web
     *
     * @param domainJwtRequest Jwt-запрос из domain
     * @return Jwt-запрос из web
     */
    public static JwtRequest fromDomainToWeb(tictactoe.domain.model.JwtRequest domainJwtRequest) {
        JwtRequest webJwtRequest = new JwtRequest();
        webJwtRequest.setLogin(domainJwtRequest.getLogin());
        webJwtRequest.setPassword(domainJwtRequest.getPassword());
        return webJwtRequest;
    }

    /**
     * Маппер Jwt-запроса из domain в web
     *
     * @param webJwtRequest Jwt-запрос из domain
     * @return Jwt-запрос из web
     */
    public static tictactoe.domain.model.JwtRequest fromWebToDomain(JwtRequest webJwtRequest) {
        tictactoe.domain.model.JwtRequest domainJwtRequest = new tictactoe.domain.model.JwtRequest();
        domainJwtRequest.setLogin(webJwtRequest.getLogin());
        domainJwtRequest.setPassword(webJwtRequest.getPassword());
        return domainJwtRequest;
    }
}
