import { StyleSheet, Text, View } from 'react-native';

import { colors, spacing } from '../../theme/tokens';

export function LandingScreen() {
  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        BackMind
      </Text>
      <Text style={styles.subtitle}>Remember what matters.</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    backgroundColor: colors.surface,
    flex: 1,
    justifyContent: 'center',
    padding: spacing.medium,
  },
  title: {
    color: colors.primary,
    fontSize: 36,
    fontWeight: '700',
  },
  subtitle: {
    color: colors.primaryPressed,
    fontSize: 17,
    marginTop: spacing.small,
  },
});
