import { api } from "./api";
import type { UserAccount } from "../types/UserAccount";

export const loginRequest = async (email: string, password: string): Promise<string> => {
    const { data } = await api.post("/auth/login", { email, password });
    return data.token;
};

export const getMe = async (): Promise<UserAccount> => {
    const { data } = await api.get("/auth/me");
    return data;
};