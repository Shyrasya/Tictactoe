package tictactoe.web.mapper;

import tictactoe.web.model.Game;

public class GameMapper {
    /**
     * Маппер игры из domain в web
     *
     * @param domainGame Игра из domain (логики)
     * @return Игра из web
     */
    public static Game fromDomainToWeb(tictactoe.domain.model.Game domainGame) {
        Game webGame = new Game();
        webGame.setBoard(GameBoardMapper.fromDomainToWeb(domainGame.getBoard()));
        webGame.setUuid(domainGame.getUuid());
        webGame.setGameCreationDate(domainGame.getGameCreationDate());

        webGame.setState(GameStateMapper.fromDomainToWeb(domainGame.getState()));
        webGame.setCurrentPlayerUuid(domainGame.getCurrentPlayerUuid());
        webGame.setWinnerUuid(domainGame.getWinnerUuid());
        webGame.setPlayerOneUuid(domainGame.getPlayerOneUuid());
        webGame.setPlayerTwoUuid(domainGame.getPlayerTwoUuid());

        webGame.setNowTurnSign(domainGame.getNowTurnSign());
        webGame.setPlayerOneSign(domainGame.getPlayerOneSign());
        webGame.setPlayerTwoSign(domainGame.getPlayerTwoSign());
        return webGame;
    }

    /**
     * Маппер игры из web в domain
     *
     * @param webGame Игра из web
     * @return Игра из domain (логики)
     */
    public static tictactoe.domain.model.Game fromWebToDomain(Game webGame) {
        tictactoe.domain.model.Game domainGame = new tictactoe.domain.model.Game();
        domainGame.setBoard(GameBoardMapper.fromWebToDomain(webGame.getBoard()));
        domainGame.setUuid(webGame.getUuid());
        domainGame.setGameCreationDate(webGame.getGameCreationDate());

        domainGame.setState(GameStateMapper.fromWebToDomain(webGame.getState()));
        domainGame.setCurrentPlayerUuid(webGame.getCurrentPlayerUuid());
        domainGame.setWinnerUuid(webGame.getWinnerUuid());
        domainGame.setPlayerOneUuid(webGame.getPlayerOneUuid());
        domainGame.setPlayerTwoUuid(webGame.getPlayerTwoUuid());

        domainGame.setNowTurnSign(webGame.getNowTurnSign());
        domainGame.setPlayerOneSign(webGame.getPlayerOneSign());
        domainGame.setPlayerTwoSign(webGame.getPlayerTwoSign());
        return domainGame;
    }
}
