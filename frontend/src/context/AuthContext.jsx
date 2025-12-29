import { createContext, useCallback, useContext, useMemo, useState } from "react";
import { loginRequest, registerRequest } from "../api/auth.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("chrms_token") || "");
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem("chrms_user");
    return stored ? JSON.parse(stored) : null;
  });
  const [status, setStatus] = useState("");

  const saveSession = useCallback((newToken, userInfo) => {
    setToken(newToken || "");
    setUser(userInfo || null);
    if (newToken) {
      localStorage.setItem("chrms_token", newToken);
    } else {
      localStorage.removeItem("chrms_token");
    }
    if (userInfo) {
      localStorage.setItem("chrms_user", JSON.stringify(userInfo));
    } else {
      localStorage.removeItem("chrms_user");
    }
  }, []);

  const login = useCallback(
    async (payload, roleHint) => {
      setStatus("Đang đăng nhập...");
      const response = await loginRequest({ email: payload.email, password: payload.password });
      saveSession(response.token, { email: payload.email, role: response.role || roleHint });
      setStatus("Đăng nhập thành công");
      return response;
    },
    [saveSession]
  );

  const register = useCallback(
    async (payload) => {
      setStatus("Đang đăng ký...");
      const response = await registerRequest(payload);
      setStatus("Đăng ký thành công");
      return response;
    },
    []
  );

  const logout = useCallback(() => {
    saveSession("", null);
    setStatus("Đã đăng xuất");
  }, [saveSession]);

  const value = useMemo(
    () => ({ token, user, status, login, register, logout, setStatus }),
    [token, user, status, login, register, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}
