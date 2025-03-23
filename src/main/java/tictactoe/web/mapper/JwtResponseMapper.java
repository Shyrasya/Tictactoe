package tictactoe.web.mapper;

import tictactoe.web.model.JwtResponse;

public class JwtResponseMapper {

    /**
     * Маппер Jwt-ответа из domain в web
     * @param domainJwtResponse Jwt-ответ из domain
     * @return Jwt-ответ из web
     */
    public static JwtResponse fromDomainToWeb(tictactoe.domain.model.JwtResponse domainJwtResponse){
        JwtResponse webJwtResponse = new JwtResponse();
        webJwtResponse.setAccessToken(domainJwtResponse.getAccessToken());
        webJwtResponse.setRefreshToken(domainJwtResponse.getRefreshToken());
        webJwtResponse.setExpiresIn(domainJwtResponse.getExpiresIn());
        return  webJwtResponse;
    }

    /**
     * Маппер Jwt-ответа из domain в web
     * @param webJwtResponse Jwt-ответ из domain
     * @return Jwt-ответ из web
     */
    public static tictactoe.domain.model.JwtResponse fromWebToDomain(JwtResponse webJwtResponse){
        tictactoe.domain.model.JwtResponse domainJwtResponse = new tictactoe.domain.model.JwtResponse();
        domainJwtResponse.setAccessToken(webJwtResponse.getAccessToken());
        domainJwtResponse.setRefreshToken(webJwtResponse.getRefreshToken());
        domainJwtResponse.setExpiresIn(webJwtResponse.getExpiresIn());
        return domainJwtResponse;
    }
}
