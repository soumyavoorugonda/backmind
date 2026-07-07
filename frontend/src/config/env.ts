const apiBaseUrl = process.env.EXPO_PUBLIC_API_URL ?? 'http://localhost:8080';

export const env = {
  apiBaseUrl,
} as const;
