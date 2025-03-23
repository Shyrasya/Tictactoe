package tictactoe.datasource.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tictactoe.datasource.model.Game;
import org.springframework.data.repository.CrudRepository;
import tictactoe.datasource.model.GameState;
import tictactoe.datasource.model.UserWinRate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface GameRepository extends CrudRepository<Game, UUID> {
    /**
     * Метод поиска игр в базе данных, в которых один из игроков соответсвует запрашиваемому uuid, к тому же игра имеет определенные состояния
     * @param playerUuid Искомый уникальный идентификатор пользователя
     * @param states Искомые состояния игры
     * @return Список игр, удовлетворяющих искомым параметрам
     */
    @Query("SELECT g FROM Game g WHERE (g.playerOneUuid = :playerUuid OR g.playerTwoUuid = :playerUuid) AND g.state IN :states")
    List<Game> findGamesByPlayerUuidAndStateIn(@Param("playerUuid") UUID playerUuid, @Param("states") List<GameState> states);

    /**
     * Поиск уникальных идентификаторов игр, у которых первый игрок имеет запрашваемый символ, к тому же игра находится в определенном состоянии
     * @param playerSign Символ первого игрока
     * @param state Искомое состояние игры
     * @return Список идентификаторов игр, удовлетворяющим искомым параметрам
     */
    @Query("SELECT g.uuid FROM Game g WHERE g.playerOneSign = :playerSign AND g.state = :state")
    List<UUID> findByPlayerOneSignAndState(@Param("playerSign") int playerSign, @Param("state") GameState state);


    /**
     * Находит завершённые игры (состояние WIN или TIE) для указанного игрока.
     *
     * @param playerUuid UUID игрока, для которого ищутся завершённые игры.
     * @return Список завершённых игр (WIN или TIE), в которых участвовал указанный игрок.
     */
    @Query("SELECT g FROM Game g WHERE (g.playerOneUuid = :playerUuid OR g.playerTwoUuid = :playerUuid) AND (g.state = tictactoe.datasource.model.GameState.TIE OR g.state = tictactoe.datasource.model.GameState.WIN)")
    List<Game> findEndGamesByPlayerUuid(@Param("playerUuid") UUID playerUuid);

    @Query("SELECT u.login FROM User u WHERE u.uuid = :userUuid")
    String getLoginByUuidUser(@Param("userUuid") UUID userUuid);

    @Query("SELECT COUNT(u) FROM User u")
    int countTotalUsers();

    /**
     * Получает список лучших игроков с их коэффициентом побед.
     *
     * @param pageable Количество записей, которое необходимо вернуть.
     * @return Список объектов UserWinRate, содержащих UUID пользователя, логин и коэффициент побед.
     */
    @Query("SELECT new tictactoe.datasource.model.UserWinRate(u.login, u.uuid, " +
            "COALESCE(CAST(SUM(CASE WHEN g.winnerUuid = u.uuid THEN 1 ELSE 0 END) AS double) / " +
            "NULLIF(SUM(CASE WHEN (g.winnerUuid IS NOT NULL AND g.winnerUuid <> u.uuid) OR " +
            "(g.winnerUuid IS NULL AND g.state = tictactoe.datasource.model.GameState.WIN) THEN 1 ELSE 0 END) + " +
            "SUM(CASE WHEN g.state = tictactoe.datasource.model.GameState.TIE THEN 1 ELSE 0 END), 0), " +
            "CAST(SUM(CASE WHEN g.winnerUuid = u.uuid THEN 1 ELSE 0 END) AS double)) AS winRate) " +
            "FROM User u " +
            "LEFT JOIN Game g ON (g.playerOneUuid = u.uuid OR g.playerTwoUuid = u.uuid) AND " +
            "(g.state = tictactoe.datasource.model.GameState.WIN OR g.state = tictactoe.datasource.model.GameState.TIE) " +
            "GROUP BY u.login, u.uuid " +
            "ORDER BY winRate DESC, u.login ASC")
    List<UserWinRate> getLeadersList(Pageable pageable);



}


