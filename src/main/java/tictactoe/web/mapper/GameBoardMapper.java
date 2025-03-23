package tictactoe.web.mapper;

import tictactoe.web.model.GameBoard;

public class GameBoardMapper {

    /**
     * Маппер игрового поля из domain в web
     *
     * @param domainGameBoard Игровое поле из domain
     * @return Игровое поле из web
     */
    public static GameBoard fromDomainToWeb(tictactoe.domain.model.GameBoard domainGameBoard) {
        GameBoard webGameBoard = new GameBoard();
        webGameBoard.setBoard(domainGameBoard.getBoard());
        return webGameBoard;
    }

    /**
     * Маппер игрового поля из web в domain
     *
     * @param webGameBoard Игровое поле из web
     * @return Игровое поле из domain
     */
    public static tictactoe.domain.model.GameBoard fromWebToDomain(GameBoard webGameBoard) {
        tictactoe.domain.model.GameBoard domainGameBoard = new tictactoe.domain.model.GameBoard();
        domainGameBoard.setBoard(webGameBoard.getBoard());
        return domainGameBoard;
    }
}
