package tictactoe.datasource.mapper;

import tictactoe.domain.model.Game;

public class GameMapper {

    /**
     * Маппер игры из domain в datasource
     *
     * @param domainGame Игра из domain (логики)
     * @return Игра из datasource
     */
    public static tictactoe.datasource.model.Game fromDomainToDataSource(Game domainGame) {
        tictactoe.datasource.model.Game dataSourceGame = new tictactoe.datasource.model.Game();
        dataSourceGame.setBoard(GameBoardMapper.fromDomainToDataSource(domainGame.getBoard()));
        dataSourceGame.setUuid(domainGame.getUuid());
        dataSourceGame.setGameCreationDate(domainGame.getGameCreationDate());

        dataSourceGame.setState(GameStateMapper.fromDomainToDataSource(domainGame.getState()));
        dataSourceGame.setCurrentPlayerUuid(domainGame.getCurrentPlayerUuid());
        dataSourceGame.setWinnerUuid(domainGame.getWinnerUuid());
        dataSourceGame.setPlayerOneUuid(domainGame.getPlayerOneUuid());
        dataSourceGame.setPlayerTwoUuid(domainGame.getPlayerTwoUuid());

        dataSourceGame.setNowTurnSign(domainGame.getNowTurnSign());
        dataSourceGame.setPlayerOneSign(domainGame.getPlayerOneSign());
        dataSourceGame.setPlayerTwoSign(domainGame.getPlayerTwoSign());
        return dataSourceGame;
    }

    /**
     * Маппер игры из datasource в domain
     *
     * @param dataSourceGame Игра из datasource
     * @return Игра из domain (логики)
     */
    public static Game fromDataSourceToDomain(tictactoe.datasource.model.Game dataSourceGame) {
        Game domainGame = new Game();
        domainGame.setBoard(GameBoardMapper.fromDataSourceToDomain(dataSourceGame.getBoard()));
        domainGame.setUuid(dataSourceGame.getUuid());
        domainGame.setGameCreationDate(dataSourceGame.getGameCreationDate());

        domainGame.setState(GameStateMapper.fromDataSourceToDomain(dataSourceGame.getState()));
        domainGame.setCurrentPlayerUuid(dataSourceGame.getCurrentPlayerUuid());
        domainGame.setWinnerUuid(dataSourceGame.getWinnerUuid());
        domainGame.setPlayerOneUuid(dataSourceGame.getPlayerOneUuid());
        domainGame.setPlayerTwoUuid(dataSourceGame.getPlayerTwoUuid());

        domainGame.setNowTurnSign(dataSourceGame.getNowTurnSign());
        domainGame.setPlayerOneSign(dataSourceGame.getPlayerOneSign());
        domainGame.setPlayerTwoSign(dataSourceGame.getPlayerTwoSign());
        return domainGame;
    }
}
