function isAccessTokenExpired() {
    const expiresIn = localStorage.getItem("expiresIn");
    if (!expiresIn || isNaN(expiresIn)) return true;
    const expirationTime = Number(expiresIn);
    const currentTime = Math.floor(Date.now() / 1000);
    return currentTime > expirationTime;
}

async function updateAccessToken() {
    const refreshToken = localStorage.getItem("refreshToken");
    const response = await fetch("/tictactoe/auth/update-access", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken })
    });

    if (response.status === 403) {
        return false;
    }

    if (response.status === 401 || !response.ok) {
        throw new Error("Ошибка обновления access-токена");
    }

    const data = await response.json();
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("expiresIn", data.expiresIn.toString());
    return true;
}

async function updateRefreshToken() {
    const accessToken = localStorage.getItem("accessToken");
    const response = await fetch("/tictactoe/auth/update-refresh", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + accessToken
        }
    });
    if (response.status === 401 || !response.ok) {
        throw new Error("Ошибка обновления access-токена");
    }
    const data = await response.json();
    localStorage.setItem("refreshToken", data.refreshToken);
}

async function checkAndRefreshToken() {
    if (isAccessTokenExpired()) {
        try {
            const isAccessTokenUpdated = await updateAccessToken();
            if (!isAccessTokenUpdated) {
                await updateRefreshToken();
            }
        } catch (error) {
            throw error;
        }
    }
}