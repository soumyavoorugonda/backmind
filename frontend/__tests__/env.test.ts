describe('environment configuration', () => {
  const originalApiUrl = process.env.EXPO_PUBLIC_API_URL;

  afterEach(() => {
    process.env.EXPO_PUBLIC_API_URL = originalApiUrl;
    jest.resetModules();
  });

  it('exposes the backend API URL from Expo public configuration', () => {
    process.env.EXPO_PUBLIC_API_URL = 'http://localhost:8080';

    const { env } = require('../src/config/env');

    expect(env.apiBaseUrl).toBe('http://localhost:8080');
  });
});
