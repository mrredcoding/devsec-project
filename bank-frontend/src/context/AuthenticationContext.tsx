import { createContext, useContext, useState } from "react";
import type { UserAccount } from "../types/UserAccount";
import { loginRequest, getMe } from "../gateway/authenticationApi";
import {authenticationStorage} from "./authenticationStorage.ts";
import type {AuthenticationResponse} from "../types/AuthenticationResponse.tsx";

type AuthenticationContextType = {
  user: UserAccount | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
};

const AuthenticationContext =
  createContext<AuthenticationContextType>(null!);

export function AuthenticationProvider({ children }: any) {
  const [user, setUser] = useState<UserAccount | null>(null);
  const [loading, setLoading] = useState(true);

  const login = async (email: string, password: string) => {
    const { token, expiresIn }: AuthenticationResponse = await loginRequest(
        email,
        password
    );

    authenticationStorage.setToken(token, expiresIn, logout);

    const me = await getMe();
    setUser(me);
  };

  const logout = () => {
    authenticationStorage.clear();
    setUser(null);
  };

  useState(() => setLoading(false));

  return (
    <AuthenticationContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthenticationContext.Provider>
  );
}

export const useAuthentication = () => useContext(AuthenticationContext);