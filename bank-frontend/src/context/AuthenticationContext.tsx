import {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";

import type { UserAccount } from "../types/UserAccount";
import type { AuthenticationResponse } from "../types/AuthenticationResponse";

import { loginRequest, logoutRequest, getMe } from "../gateway/authenticationApi";
import { authenticationStorage } from "./authenticationStorage";
import { setLogoutHandler } from "../gateway/api";

type AuthenticationContextType = {
  user: UserAccount | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
};

const AuthenticationContext =
  createContext<AuthenticationContextType>(null!);

export function AuthenticationProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserAccount | null>(null);
  const [loading, setLoading] = useState(true);

  const logout = async () => {
      await logoutRequest();
      authenticationStorage.clear();
      setUser(null);
  };

  useEffect(() => {
    setLogoutHandler(logout);
  }, []);

  useEffect(() => {
    const restore = async () => {
      try {
        const token = authenticationStorage.getToken();

        if (!token) return;

        const me = await getMe();
        setUser(me);
      } catch {
        logout();
      } finally {
        setLoading(false);
      }
    };

    restore();
  }, []);

  const login = async (email: string, password: string) => {
    const { token, expiresIn }: AuthenticationResponse = await loginRequest(email, password);

    authenticationStorage.setToken(token, expiresIn, logout);

    const me = await getMe();
    setUser(me);
  };

  return (
    <AuthenticationContext.Provider
      value={{ user, login, logout, loading }}
    >
      {children}
    </AuthenticationContext.Provider>
  );
}

export const useAuthentication = () => useContext(AuthenticationContext);