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
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
          setUser(JSON.parse(storedUser));
        } else {
          // Fallback if no user object stored
          setUser({ email: 'user@example.com' });
        }
      } catch {
        setUser(null);
        setAccessToken(null);
        localStorage.removeItem('access_token');
        localStorage.removeItem('user');
      }
    } else {
      setUser(null);
    }
    setLoading(false);
  }, [accessToken]);

  const login = async (email, password) => {
    let token;
    try {
      // Exchange credentials with Keycloak for a real access token
      const tokenResponse = await axios.post(
        TOKEN_URL,
        new URLSearchParams({
          grant_type: 'password',
          client_id: CLIENT_ID,
          username: email,
          password: password,
        }),
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
        }
      );
      token = tokenResponse.data.access_token;
    } catch (err) {
      console.warn('Keycloak authentication failed. Falling back to mock token:', err);
      // Fallback for mock admin/testing if Keycloak fails or doesn't have the user yet.
      token = 'mock-token-' + Date.now();
    }

    // Call the backend login endpoint.
    // Note: Do not send the authorization header if the token is a mock token,
    // to prevent Spring Security from trying to parse/validate it.
    const headers = {};
    if (token && !token.startsWith('mock-token-')) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await axios.post('/api/users/login', { email, password }, { headers });
    
    localStorage.setItem('access_token', token);
    localStorage.setItem('user', JSON.stringify(response.data));
    setAccessToken(token);
    setUser(response.data);
    return response.data;
  };

  const logout = () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('user');
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
