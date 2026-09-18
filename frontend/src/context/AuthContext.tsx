import { createContext, useContext, useState, type ReactNode } from "react";
import * as authApi from "../api/auth";

interface AuthContextValue {
  email: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [email, setEmail] = useState<string | null>(() => localStorage.getItem("email"));

  function persist(token: string, email: string) {
    localStorage.setItem("token", token);
    localStorage.setItem("email", email);
    setEmail(email);
  }

  async function login(email: string, password: string) {
    const res = await authApi.login(email, password);
    persist(res.token, res.email);
  }

  async function register(email: string, password: string) {
    const res = await authApi.register(email, password);
    persist(res.token, res.email);
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    setEmail(null);
  }

  return (
    <AuthContext.Provider value={{ email, isAuthenticated: !!email, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
