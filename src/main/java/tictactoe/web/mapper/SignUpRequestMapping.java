package tictactoe.web.mapper;

import tictactoe.web.model.SignUpRequest;

public class SignUpRequestMapping {
    /**
     * Маппер регистрации нового пользователя из domain в web
     *
     * @param domainSignUpRequest Регистрация нового пользователя из domain
     * @return Регистрация нового пользователя из web
     */
    public static SignUpRequest fromDomainToWeb(tictactoe.domain.model.SignUpRequest domainSignUpRequest) {
        SignUpRequest webSignUpRequest = new SignUpRequest();
        webSignUpRequest.setLogin(domainSignUpRequest.getLogin());
        webSignUpRequest.setPassword(domainSignUpRequest.getPassword());
        return webSignUpRequest;
    }

    /**
     * Маппер регистрации нового пользователя из domain в web
     *
     * @param webSignUpRequest Регистрация нового пользователя из domain
     * @return Регистрация нового пользователя из web
     */
    public static tictactoe.domain.model.SignUpRequest fromWebToDomain(SignUpRequest webSignUpRequest) {
        tictactoe.domain.model.SignUpRequest domainSignUpRequest = new tictactoe.domain.model.SignUpRequest();
        domainSignUpRequest.setLogin(webSignUpRequest.getLogin());
        domainSignUpRequest.setPassword(webSignUpRequest.getPassword());
        return domainSignUpRequest;
    }
}
