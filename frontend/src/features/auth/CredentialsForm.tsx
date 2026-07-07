import { useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

import { Button } from '../../components/ui/Button';
import { colors, radii, spacing } from '../../theme/tokens';

export type Credentials = {
  email: string;
  password: string;
};

type CredentialsFormProps = {
  actionLabel: string;
  error?: string | null;
  loading?: boolean;
  passwordAutoComplete: 'current-password' | 'new-password';
  title: string;
  onSubmit: (credentials: Credentials) => void;
};

export function CredentialsForm({
  actionLabel,
  error = null,
  loading = false,
  passwordAutoComplete,
  title,
  onSubmit,
}: CredentialsFormProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordVisible, setPasswordVisible] = useState(false);

  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        {title}
      </Text>
      <TextInput
        accessibilityLabel="Email"
        autoCapitalize="none"
        autoComplete="email"
        keyboardType="email-address"
        onChangeText={setEmail}
        placeholder="Email"
        style={styles.input}
        value={email}
      />
      <View style={styles.passwordField}>
        <TextInput
          accessibilityLabel="Password"
          autoCapitalize="none"
          autoComplete={passwordAutoComplete}
          onChangeText={setPassword}
          placeholder="Password"
          secureTextEntry={!passwordVisible}
          style={[styles.input, styles.passwordInput]}
          value={password}
        />
        <Pressable
          accessibilityLabel={passwordVisible ? 'Hide password' : 'Show password'}
          accessibilityRole="button"
          hitSlop={8}
          onPress={() => setPasswordVisible((visible) => !visible)}
          style={styles.visibilityButton}
        >
          <Text accessibilityElementsHidden style={styles.visibilityIcon}>
            {passwordVisible ? '⊘' : '👁'}
          </Text>
        </Pressable>
      </View>
      {error && (
        <Text accessibilityRole="alert" style={styles.error}>
          {error}
        </Text>
      )}
      {loading && <ActivityIndicator accessibilityLabel="Submitting" color={colors.primary} />}
      <Button
        disabled={loading}
        label={actionLabel}
        onPress={() => onSubmit({ email, password })}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.surface,
    flex: 1,
    gap: spacing.medium,
    justifyContent: 'center',
    padding: spacing.medium,
  },
  title: {
    color: colors.primary,
    fontSize: 30,
    fontWeight: '700',
  },
  input: {
    borderColor: colors.primary,
    borderRadius: radii.medium,
    borderWidth: 1,
    fontSize: 16,
    minHeight: 48,
    paddingHorizontal: spacing.medium,
  },
  passwordField: {
    position: 'relative',
  },
  passwordInput: {
    paddingRight: 56,
  },
  visibilityButton: {
    alignItems: 'center',
    bottom: 0,
    justifyContent: 'center',
    position: 'absolute',
    right: 0,
    top: 0,
    width: 52,
  },
  visibilityIcon: {
    color: colors.text,
    fontSize: 20,
  },
  error: {
    color: '#B42318',
  },
});
