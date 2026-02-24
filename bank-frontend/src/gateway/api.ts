import axios from "axios";
import {authenticationStorage} from "../context/authenticationStorage.ts";

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
      if (err.response?.status === 401) {
        authenticationStorage.clear();
        window.location.href = "/login";
      }
      return Promise.reject(err);
    }
);