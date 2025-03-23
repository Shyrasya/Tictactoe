package tictactoe.datasource.mapper;

import tictactoe.domain.model.GameBoard;

public class GameBoardMapper {

    /**
     * Маппер игрового поля из domain в datasource
     *
     * @param domainGameBoard Игровое поле из domain (логики)
     * @return Игровое поле из datasource
     */
    public static tictactoe.datasource.model.GameBoard fromDomainToDataSource(GameBoard domainGameBoard) {
        tictactoe.datasource.model.GameBoard dataSourceGameBoard = new tictactoe.datasource.model.GameBoard();
        dataSourceGameBoard.setBoard(domainGameBoard.getBoard());
        return dataSourceGameBoard;
    }

    /**
     * Маппер игрового поля из datasource в domain
     *
     * @param dataSourceGameBoard Игровое поле из datasource
     * @return Игровое поле из domain (логики)
     */
    public static GameBoard fromDataSourceToDomain(tictactoe.datasource.model.GameBoard dataSourceGameBoard) {
        GameBoard domainGameBoard = new GameBoard();
        domainGameBoard.setBoard(dataSourceGameBoard.getBoard());
        return domainGameBoard;
    }
}
