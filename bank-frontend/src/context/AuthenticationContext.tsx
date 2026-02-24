import { createContext, useContext, useEffect, useState } from "react";
import type { UserAccount } from "../types/UserAccount";
import {loginRequest, getMe, logoutRequest} from "../gateway/authenticationApi";

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
    const token = await loginRequest(email, password);

    localStorage.setItem("token", token);

    const me = await getMe();
    setUser(me);
  };

  const logout = async() => {
    await logoutRequest();
    localStorage.removeItem("token");
    setUser(null);
  };

  useEffect(() => {
    const init = async () => {
      const token = localStorage.getItem("token");

      if (!token) {
        setLoading(false);
        return;
      }

      try {
        const me = await getMe();
        setUser(me);
      } catch {
        logout();
      } finally {
        setLoading(false);
      }
    };

    init();
  }, []);

  return (
    <AuthenticationContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthenticationContext.Provider>
  );
}

export const useAuthentication = () => useContext(AuthenticationContext);