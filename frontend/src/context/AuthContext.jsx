import { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const KEYCLOAK_URL = 'http://localhost:8181';
const REALM = 'E-Commerce-Microservices-Project';
const CLIENT_ID = 'e-commerce-frontend';
const TOKEN_URL = `${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token`;

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(localStorage.getItem('access_token'));
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (accessToken) {
      try {
        const payload = JSON.parse(atob(accessToken.split('.')[1]));
        setUser({
          email: payload.email,
          name: payload.name || payload.preferred_username,
          roles: payload.realm_access?.roles || [],
          sub: payload.sub,
        });
      } catch {
        setUser(null);
        setAccessToken(null);
        localStorage.removeItem('access_token');
      }
    } else {
      setUser(null);
    }
    setLoading(false);
  }, [accessToken]);

  const login = async (email, password) => {
    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('client_id', CLIENT_ID);
    params.append('username', email);
    params.append('password', password);

    const response = await axios.post(TOKEN_URL, params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    });

    const token = response.data.access_token;
    localStorage.setItem('access_token', token);
    setAccessToken(token);
    return response.data;
  };

  const logout = () => {
    localStorage.removeItem('access_token');
    setAccessToken(null);
    setUser(null);
  };

  const isAuthenticated = !!accessToken;

  return (
    <AuthContext.Provider value={{ user, accessToken, login, logout, isAuthenticated, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
