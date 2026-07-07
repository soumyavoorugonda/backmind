import * as SecureStore from 'expo-secure-store';

const AUTH_TOKEN_KEY = 'backmind.authToken';

export const tokenStorage = {
  save(token: string) {
    return SecureStore.setItemAsync(AUTH_TOKEN_KEY, token);
  },

  read() {
    return SecureStore.getItemAsync(AUTH_TOKEN_KEY);
  },

  clear() {
    return SecureStore.deleteItemAsync(AUTH_TOKEN_KEY);
  },
} as const;
