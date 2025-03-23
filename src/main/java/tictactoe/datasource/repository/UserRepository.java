package tictactoe.datasource.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tictactoe.datasource.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    /**
     * Метод проверки наличия пользователя в базе данных по логину
     * @param login Искомый логин
     * @return true - существует, false - нет
     */
    boolean existsByLogin(String login);

    /**
     * Метод поиска объекта класса {@link User} в базе данных по логину
     * @param login Введенный логин
     * @return Искомый пользователь
     */
    User findByLogin(String login);

    /**
     * Метод поиска объекта класса {@link User} в базе данных по UUID
     * @param userUuid Введенный Uuid
     * @return Искомый пользователь
     */
    User findByUuid(UUID userUuid);

    /**
     * Обновляет refresh-токен для пользователя с указанным UUID.
     *
     * @param uuid UUID пользователя, для которого нужно обновить refresh-токен.
     * @param refreshToken Новый refresh-токен, который будет сохранён.
     */
    @Modifying
    @Query("UPDATE User u SET u.refreshToken = :refreshToken WHERE u.uuid = :uuid")
    void updateRefreshTokenByUuid(@Param("uuid") UUID uuid, @Param("refreshToken") String refreshToken);

    /**
     * Удаляет refresh-токен для пользователя с указанным UUID.
     *
     * @param uuid UUID пользователя, у которого нужно удалить refresh-токен.
     */
    @Modifying
    @Query("UPDATE User u SET u.refreshToken = NULL WHERE u.uuid =:uuid")
    void deleteRefreshTokenByUuid(@Param("uuid") UUID uuid);
}
