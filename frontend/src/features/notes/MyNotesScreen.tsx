import { ActivityIndicator, ScrollView, StyleSheet, Text, View } from 'react-native';

import { colors, spacing } from '../../theme/tokens';

export type MyNote = {
  id: string;
  content: string;
  category: string | null;
  createdAt: string;
};

type MyNotesScreenProps = {
  error?: string | null;
  loading?: boolean;
  notes: MyNote[];
};

export function MyNotesScreen({ error, loading = false, notes }: MyNotesScreenProps) {
  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>My notes</Text>
      {loading && <ActivityIndicator accessibilityLabel="Loading notes" color={colors.primary} />}
      {error && <Text accessibilityRole="alert" style={styles.error}>{error}</Text>}
      {notes.map((note) => (
        <View key={note.id} style={styles.note}>
          {note.category && <Text style={styles.category}>{note.category}</Text>}
          <Text style={styles.content}>{note.content}</Text>
          <Text style={styles.date}>{formatDate(note.createdAt)}</Text>
        </View>
      ))}
    </ScrollView>
  );
}

function formatDate(createdAt: string) {
  return new Intl.DateTimeFormat('en-US', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  }).format(new Date(createdAt));
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.surface,
    gap: spacing.medium,
    padding: spacing.medium,
  },
  title: {
    color: colors.primary,
    fontSize: 30,
    fontWeight: '700',
  },
  note: {
    backgroundColor: colors.surfaceMuted,
    borderRadius: 10,
    gap: spacing.small,
    padding: spacing.medium,
  },
  category: {
    color: colors.textMuted,
    fontSize: 14,
    fontWeight: '600',
  },
  content: {
    color: colors.text,
    fontSize: 16,
    lineHeight: 23,
  },
  date: {
    color: colors.textMuted,
    fontSize: 12,
  },
  error: {
    color: '#B42318',
  },
});
