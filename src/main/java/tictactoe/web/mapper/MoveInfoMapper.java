package tictactoe.web.mapper;

import tictactoe.web.model.Game;
import tictactoe.web.model.MoveInfo;

public class MoveInfoMapper {

    /**
     * Маппер класса с игрой и сообщением о статусе из domain в web
     *
     * @param domainServerMoveInfo Объект класса ServerMoveInfo из domain
     * @return Объект класса ServerMoveInfo из web
     */
    public static MoveInfo fromDomainToWeb(tictactoe.domain.model.MoveInfo domainServerMoveInfo) {
        Game webGame = GameMapper.fromDomainToWeb(domainServerMoveInfo.getGame());
        return new MoveInfo(domainServerMoveInfo.getMessage(), webGame);
    }

    /**
     * Маппер класса с игрой и сообщением о статусе из web в domain
     *
     * @param webServerMoveInfo Объект класса ServerMoveInfo из web
     * @return Объект класса ServerMoveInfo из domain
     */
    public static tictactoe.domain.model.MoveInfo fromWebToDomain(MoveInfo webServerMoveInfo) {
        tictactoe.domain.model.Game domainGame = GameMapper.fromWebToDomain(webServerMoveInfo.getGame());
        return new tictactoe.domain.model.MoveInfo(webServerMoveInfo.getMessage(), domainGame);
    }

}
