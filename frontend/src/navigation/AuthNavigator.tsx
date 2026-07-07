import { useState } from 'react';
import { StyleSheet, View } from 'react-native';

import { Button } from '../components/ui/Button';
import { LoginCredentials, LoginScreen } from '../features/auth/LoginScreen';
import { SignupCredentials, SignupScreen } from '../features/auth/SignupScreen';
import { colors, spacing } from '../theme/tokens';

type AuthNavigatorProps = {
  error?: string | null;
  loading?: boolean;
  onLogin?: (credentials: LoginCredentials) => void;
  onSignup?: (credentials: SignupCredentials) => void;
};

export function AuthNavigator({
  error = null,
  loading = false,
  onLogin = () => undefined,
  onSignup = () => undefined,
}: AuthNavigatorProps) {
  const [screen, setScreen] = useState<'login' | 'signup'>('login');

  if (screen === 'signup') {
    return (
      <View style={styles.container}>
        <SignupScreen error={error} loading={loading} onSubmit={onSignup} />
        <Button label="Back to login" onPress={() => setScreen('login')} />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <LoginScreen error={error} loading={loading} onSubmit={onLogin} />
      <Button label="Sign up" onPress={() => setScreen('signup')} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.surface,
    flex: 1,
    gap: spacing.small,
    paddingBottom: spacing.medium,
    paddingHorizontal: spacing.medium,
  },
});
