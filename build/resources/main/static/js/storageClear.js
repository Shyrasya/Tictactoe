function clearLocalStorage() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("expiresIn");
    localStorage.removeItem("opponent");
    localStorage.removeItem("playerChoice");
    localStorage.removeItem("activeUserGameUuid");
    localStorage.removeItem("gameUuid");
    localStorage.removeItem("errorMessage");
}
