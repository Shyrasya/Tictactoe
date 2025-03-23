package tictactoe.domain.model;

public class RefreshJwtRequest {

    /**
     * Токен обновления
     */
    String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
