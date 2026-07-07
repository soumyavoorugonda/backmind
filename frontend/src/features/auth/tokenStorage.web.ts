const AUTH_TOKEN_KEY = 'backmind.authToken';

export const tokenStorage = {
  async save(token: string) {
    globalThis.localStorage.setItem(AUTH_TOKEN_KEY, token);
  },

  async read() {
    return globalThis.localStorage.getItem(AUTH_TOKEN_KEY);
  },

  async clear() {
    globalThis.localStorage.removeItem(AUTH_TOKEN_KEY);
  },
} as const;
