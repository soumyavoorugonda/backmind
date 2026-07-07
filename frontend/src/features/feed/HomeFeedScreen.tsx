import { Ionicons } from '@expo/vector-icons';
import { ComponentProps, Fragment } from 'react';
import { ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';

import { colors, spacing } from '../../theme/tokens';

export type FeedNote = {
  id: string;
  content: string;
  category: string;
  createdAt?: string;
};

export type ReviewAction = 'USEFUL' | 'NOT_USEFUL' | 'STILL_BELIEVE' | 'FORGOT_THIS';

type HomeFeedScreenProps = {
  error?: string | null;
  loading?: boolean;
  notes: FeedNote[];
  onReview: (noteId: string, action: ReviewAction) => void;
};

type IoniconName = ComponentProps<typeof Ionicons>['name'];

const reviewActions: ReadonlyArray<{ label: string; icon: IoniconName; action: ReviewAction }> = [
  { label: 'Useful', icon: 'thumbs-up-outline', action: 'USEFUL' },
  { label: 'Not useful', icon: 'thumbs-down-outline', action: 'NOT_USEFUL' },
  { label: 'Still believe this', icon: 'heart-outline', action: 'STILL_BELIEVE' },
  { label: 'I forgot this', icon: 'help-outline', action: 'FORGOT_THIS' },
];

export function HomeFeedScreen({ error, loading = false, notes, onReview }: HomeFeedScreenProps) {
  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Today
      </Text>
      {loading && <ActivityIndicator accessibilityLabel="Loading feed" color={colors.primary} />}
      {error && (
        <Text accessibilityRole="alert" style={styles.error}>
          {error}
        </Text>
      )}
      {notes.map((note, index) => (
        <Fragment key={note.id}>
        <View style={styles.card} testID={`resurfacing-note-card-${note.id}`}>
          <View style={styles.cardHeader}>
            <View style={styles.avatar}>
              <Text style={styles.avatarLabel}>{note.category.charAt(0).toUpperCase()}</Text>
            </View>
            <View style={styles.metadata}>
              <Text style={styles.category}>{note.category}</Text>
              {note.createdAt && <Text style={styles.date}>{formatDate(note.createdAt)}</Text>}
            </View>
          </View>
          <Text style={styles.content}>{note.content}</Text>
          <View style={styles.actions}>
            {reviewActions.map(({ label, icon, action }) => (
              <Pressable
                accessibilityLabel={label}
                accessibilityRole="button"
                key={action}
                onPress={() => onReview(note.id, action)}
                style={({ pressed }) => [styles.actionButton, pressed && styles.actionPressed]}
              >
                <Ionicons color={colors.text} name={icon} size={25} />
              </Pressable>
            ))}
          </View>
        </View>
        {index < notes.length - 1 && (
          <View style={styles.separator} testID={`feed-card-separator-${index + 1}`} />
        )}
        </Fragment>
      ))}
    </ScrollView>
  );
}

function formatDate(createdAt: string) {
  return new Intl.DateTimeFormat('en-US', {
    day: 'numeric',
    month: 'short',
    timeZone: 'UTC',
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
  card: {
    backgroundColor: colors.surfaceMuted,
    borderRadius: 10,
    borderWidth: 0,
    gap: spacing.medium,
    minHeight: 200,
    padding: 20,
  },
  cardHeader: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: spacing.medium,
  },
  avatar: {
    alignItems: 'center',
    borderColor: colors.text,
    borderRadius: 28,
    borderWidth: 1.5,
    height: 40,
    justifyContent: 'center',
    width: 40,
  },
  avatarLabel: {
    color: colors.primary,
    fontSize: 22,
    fontWeight: '700',
  },
  metadata: {
    gap: 2,
  },
  category: {
    color: colors.text,
    fontSize: 16,
    fontWeight: '600',
  },
  date: {
    color: colors.textMuted,
    fontSize: 14,
  },
  content: {
    color: colors.text,
    fontSize: 15,
    flex: 1,
    lineHeight: 28,
    textAlign: 'left',
    textAlignVertical: 'center',
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  separator: {
    backgroundColor: colors.placeholder,
    height: StyleSheet.hairlineWidth,
    marginHorizontal: -spacing.medium,
  },
  actionButton: {
    alignItems: 'center',
    backgroundColor: 'transparent',
    borderWidth: 0,
    height: 56,
    justifyContent: 'center',
    width: 56,
  },
  actionPressed: {
    backgroundColor: colors.surfaceMuted,
    transform: [{ scale: 0.96 }],
  },
  error: {
    color: '#B42318',
  },
});
