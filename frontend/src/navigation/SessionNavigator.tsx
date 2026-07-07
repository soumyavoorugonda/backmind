import { useState } from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';

import { useAuth } from '../features/auth/AuthContext';
import { Credentials } from '../features/auth/CredentialsForm';
import { authApi } from '../features/auth/authApi';
import { colors } from '../theme/tokens';
import { AppNavigator } from './AppNavigator';
import { AuthNavigator } from './AuthNavigator';

export function SessionNavigator() {
  const { status, token, signIn, signOut } = useAuth();
  const [authError, setAuthError] = useState<string | null>(null);
  const [authLoading, setAuthLoading] = useState(false);

  async function authenticate(credentials: Credentials) {
    await runAuthRequest(async () => {
      const response = await authApi.login(credentials);
      await signIn(response.token);
    });
  }

  async function signup(credentials: Credentials) {
    await runAuthRequest(async () => {
      await authApi.signup(credentials);
      const response = await authApi.login(credentials);
      await signIn(response.token);
    });
  }

  async function runAuthRequest(request: () => Promise<void>) {
    setAuthError(null);
    setAuthLoading(true);
    try {
      await request();
    } catch (error) {
      setAuthError(error instanceof Error ? error.message : 'Request failed');
    } finally {
      setAuthLoading(false);
    }
  }

  if (status === 'loading') {
    return (
      <View style={styles.loading}>
        <ActivityIndicator accessibilityLabel="Loading session" color={colors.primary} />
      </View>
    );
  }

  if (status === 'unauthenticated') {
    return (
      <AuthNavigator
        error={authError}
        loading={authLoading}
        onLogin={(credentials) => void authenticate(credentials)}
        onSignup={(credentials) => void signup(credentials)}
      />
    );
  }

  return (
    <View style={styles.authenticated}>
      <AppNavigator
        onLogout={() => {
          if (token) void authApi.logout(token).finally(signOut);
          else void signOut();
        }}
        token={token ?? ''}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  authenticated: {
    flex: 1,
  },
  loading: {
    alignItems: 'center',
    backgroundColor: colors.surface,
    flex: 1,
    justifyContent: 'center',
  },
});
