package tictactoe.web.model;

public class JwtResponse {

    /**
     * Тип авторизации для JWT
     */
    private final String type = "Bearer";

    /**
     * Сессионный токен
     */
    private String accessToken;

    /**
     * Токен обновления
     */
    private String refreshToken;

    /**
     * Время истечения срока годности access-токена (Unix timestamp (в секундах) - время в секундах с начала эпохи)
     */
    private long expiresIn;


    /**
     * Базовый конструктор класса JwtResponse
     */
    public JwtResponse() {
    }

    /**
     * Параметризированный конструктор класса JwtResponse
     * @param accessToken Сессионный токен
     * @param refreshToken Токен обновления
     * @param expiresIn Время истечения срока годности access-токена (Unix timestamp (в секундах) - время в секундах с начала эпохи)
     */
    public JwtResponse(String accessToken, String refreshToken, long expiresIn, String login) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getType() {
        return type;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
