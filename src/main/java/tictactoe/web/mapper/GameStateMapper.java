package tictactoe.web.mapper;

import tictactoe.web.model.GameState;

public class GameStateMapper {

    /**
     * Маппер игрового состояния из domain в web
     *
     * @param domainGameState Игровое состояние из domain (логики)
     * @return Игровое состояние из web
     */
    public static GameState fromDomainToWeb(tictactoe.domain.model.GameState domainGameState) {
        return GameState.valueOf(domainGameState.name());
    }

    /**
     * Маппер игрового состояния из web в domain
     *
     * @param webGameState Игровое состояние из web
     * @return Игровое состояние из domain (логики)
     */
    public static tictactoe.domain.model.GameState fromWebToDomain(GameState webGameState) {
        return tictactoe.domain.model.GameState.valueOf(webGameState.name());
    }

}
