import { Pressable, StyleSheet, Text } from 'react-native';

import { colors, radii, spacing } from '../../theme/tokens';

type ButtonProps = {
  label: string;
  onPress: () => void;
};

export function Button({ label, onPress }: ButtonProps) {
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={label}
      onPress={onPress}
      style={({ pressed }) => [styles.button, pressed && styles.pressed]}
    >
      <Text style={styles.label}>{label}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    alignItems: 'center',
    backgroundColor: colors.primary,
    borderRadius: radii.medium,
    justifyContent: 'center',
    minHeight: 48,
    paddingHorizontal: spacing.medium,
    paddingVertical: spacing.small,
  },
  pressed: {
    backgroundColor: colors.primaryPressed,
  },
  label: {
    color: colors.textOnPrimary,
    fontSize: 16,
    fontWeight: '600',
  },
});
