import { api } from "./api";
import type { UserAccount } from "../types/UserAccount";
import type { AuthenticationResponse } from "../types/AuthenticationResponse";

export const loginRequest = async (email: string, password: string): Promise<AuthenticationResponse> => {
    const { data } = await api.post<AuthenticationResponse>("/auth/login", { email, password });
    return data;
};

export const logoutRequest = async (): Promise<void> => {
    await api.post("/auth/logout");
};

export const getMe = async (): Promise<UserAccount> => {
    const { data } = await api.get("/auth/me");
    return data;
};