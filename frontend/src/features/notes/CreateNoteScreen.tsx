import { useState } from 'react';
import { StyleSheet, Text, TextInput, View } from 'react-native';

import { Button } from '../../components/ui/Button';
import { colors, radii, spacing } from '../../theme/tokens';

export type CreateNoteInput = {
  content: string;
  category: string;
};

type CreateNoteScreenProps = {
  error?: string | null;
  loading?: boolean;
  onSubmit: (note: CreateNoteInput) => void;
  success?: string | null;
};

export function CreateNoteScreen({ error, loading = false, onSubmit, success }: CreateNoteScreenProps) {
  const [content, setContent] = useState('');
  const [category, setCategory] = useState('');

  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        New note
      </Text>
      <TextInput
        accessibilityLabel="Note content"
        maxLength={300}
        multiline
        onChangeText={setContent}
        placeholder="What do you want to remember?"
        placeholderTextColor={colors.placeholder}
        style={[styles.input, styles.contentInput]}
        textAlignVertical="top"
        value={content}
      />
      <TextInput
        accessibilityLabel="Category"
        maxLength={30}
        onChangeText={setCategory}
        placeholder="Category (optional)"
        placeholderTextColor={colors.placeholder}
        style={styles.input}
        value={category}
      />
      {error && <Text accessibilityRole="alert" style={styles.error}>{error}</Text>}
      {success && <Text accessibilityLiveRegion="polite" style={styles.success}>{success}</Text>}
      <Button
        disabled={loading}
        label={loading ? 'Saving note' : 'Save note'}
        onPress={() => onSubmit({ content, category })}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.surface,
    flex: 1,
    gap: spacing.medium,
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
    color: colors.text,
    fontSize: 16,
    minHeight: 48,
    padding: spacing.medium,
  },
  contentInput: {
    minHeight: 160,
  },
  error: {
    color: '#B42318',
  },
  success: {
    color: colors.primaryPressed,
  },
});
