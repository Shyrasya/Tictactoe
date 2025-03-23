package tictactoe.datasource.mapper;

import tictactoe.domain.model.GameState;

public class GameStateMapper {
    /**
     * Маппер игрового состояния из domain в datasource
     *
     * @param domainGameState Игровое состояние из domain (логики)
     * @return  Игровое состояние из datasource
     */
    public static tictactoe.datasource.model.GameState fromDomainToDataSource(GameState domainGameState){
        return tictactoe.datasource.model.GameState.valueOf(domainGameState.name());
    }

    /**
     * Маппер игрового состояния из datasource в domain
     *
     * @param dataSourceState Игровое состояние из datasource
     * @return Игровое состояние из domain (логики)
     */
    public static GameState fromDataSourceToDomain(tictactoe.datasource.model.GameState dataSourceState) {
        return GameState.valueOf(dataSourceState.name());
    }
}
