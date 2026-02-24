let accessToken: string | null = null;
let logoutTimer: number | null = null;

export const authenticationStorage = {
    setToken: (token: string, expiresIn: number, logout: () => void) => {
        accessToken = token;

        if (logoutTimer) clearTimeout(logoutTimer);
        logoutTimer = window.setTimeout(logout, expiresIn);
    },

    getToken: () => accessToken,

    clear: () => {
        accessToken = null;
        if (logoutTimer) {
            clearTimeout(logoutTimer);
            logoutTimer = null;
        }
    },
};