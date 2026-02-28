import axios from "axios";
import { authenticationStorage } from "../context/authenticationStorage";

let logoutHandler: (() => void) | null = null;

export const setLogoutHandler = (handler: () => void) => {
  logoutHandler = handler;
};

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "https://localhost:5000",
});


api.interceptors.request.use((config) => {
  const token = authenticationStorage.getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});


api.interceptors.response.use(
  (res) => res,
  (err) => {
    const token = authenticationStorage.getToken();
    const isLoginCall = err.config?.url?.includes("/login");

    if (err.response?.status === 401 && token && !isLoginCall) {
      logoutHandler?.();
    }

    return Promise.reject(err);
  }
);