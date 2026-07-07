import {
  PropsWithChildren,
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';

import { tokenStorage } from './tokenStorage';

export type AuthStatus = 'loading' | 'authenticated' | 'unauthenticated';

type AuthContextValue = {
  status: AuthStatus;
  token: string | null;
  signIn: (token: string) => Promise<void>;
  signOut: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: PropsWithChildren) {
  const [token, setToken] = useState<string | null>(null);
  const [status, setStatus] = useState<AuthStatus>('loading');

  useEffect(() => {
    let active = true;

    tokenStorage.read().then((storedToken) => {
      if (active) {
        setToken(storedToken);
        setStatus(storedToken ? 'authenticated' : 'unauthenticated');
      }
    });

    return () => {
      active = false;
    };
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      status,
      token,
      async signIn(nextToken) {
        await tokenStorage.save(nextToken);
        setToken(nextToken);
        setStatus('authenticated');
      },
      async signOut() {
        await tokenStorage.clear();
        setToken(null);
        setStatus('unauthenticated');
      },
    }),
    [status, token],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
