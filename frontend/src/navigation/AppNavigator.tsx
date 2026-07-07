import { Ionicons } from '@expo/vector-icons';
import { useEffect, useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { HomeFeedScreen } from '../features/feed/HomeFeedScreen';
import { CreateNoteScreen } from '../features/notes/CreateNoteScreen';
import { MyNotesScreen } from '../features/notes/MyNotesScreen';
import { useNoteFlow } from '../features/notes/useNoteFlow';
import { colors, spacing } from '../theme/tokens';

type AppScreen = 'home' | 'createNote' | 'myNotes';

type AppNavigatorProps = {
  onLogout: () => void;
  token?: string;
};

const navigationItems: ReadonlyArray<{ label: string; screen: AppScreen }> = [
  { label: 'Feed', screen: 'home' },
  { label: 'New note', screen: 'createNote' },
  { label: 'My notes', screen: 'myNotes' },
];

export function AppNavigator({ onLogout, token = '' }: AppNavigatorProps) {
  const [activeScreen, setActiveScreen] = useState<AppScreen>('home');
  const [profileMenuOpen, setProfileMenuOpen] = useState(false);
  const noteFlow = useNoteFlow(token);

  useEffect(() => {
    if (token) void noteFlow.loadFeed();
  }, [token]);

  return (
    <View style={styles.container}>
      <Pressable
        onHoverOut={() => setProfileMenuOpen(false)}
        style={styles.profileArea}
        testID="profile-menu-area"
      >
        <Pressable
          accessibilityLabel={profileMenuOpen ? 'Close profile menu' : 'Open profile menu'}
          accessibilityRole="button"
          hitSlop={4}
          onPress={() => setProfileMenuOpen((open) => !open)}
          style={styles.profileButton}
        >
          <Ionicons color={colors.textOnPrimary} name="person-outline" size={20} />
        </Pressable>
        {profileMenuOpen && (
          <View style={styles.profileMenu}>
            <Pressable
              accessibilityLabel="Log out"
              accessibilityRole="button"
              onPress={() => {
                setProfileMenuOpen(false);
                onLogout();
              }}
              style={styles.menuItem}
            >
              <Text style={styles.menuLabel}>Log out</Text>
            </Pressable>
          </View>
        )}
      </Pressable>
      <View style={styles.content}>{renderScreen(activeScreen, noteFlow)}</View>
      <View accessibilityRole="tablist" style={styles.navigation}>
        {navigationItems.map(({ label, screen }) => (
          <Pressable
            accessibilityLabel={label}
            accessibilityRole="button"
            key={screen}
            onPress={() => {
              setActiveScreen(screen);
              if (screen === 'home') void noteFlow.loadFeed();
              if (screen === 'myNotes') void noteFlow.loadAll();
            }}
            style={styles.navigationItem}
          >
            <Text style={activeScreen === screen ? styles.activeLabel : styles.label}>{label}</Text>
          </Pressable>
        ))}
      </View>
    </View>
  );
}

function renderScreen(screen: AppScreen, noteFlow: ReturnType<typeof useNoteFlow>) {
  switch (screen) {
    case 'createNote':
      return (
        <CreateNoteScreen
          error={noteFlow.error}
          loading={noteFlow.saving}
          onSubmit={(note) => void noteFlow.create(note)}
          success={noteFlow.success}
        />
      );
    case 'myNotes':
      return (
        <MyNotesScreen
          error={noteFlow.error}
          loading={noteFlow.listLoading}
          notes={noteFlow.allNotes}
        />
      );
    default:
      return (
        <HomeFeedScreen
          error={noteFlow.error}
          loading={noteFlow.feedLoading}
          notes={noteFlow.notes}
          onReview={(noteId, action) => void noteFlow.review(noteId, action)}
        />
      );
  }
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.surface,
    flex: 1,
  },
  content: {
    flex: 1,
  },
  profileArea: {
    position: 'absolute',
    right: spacing.medium,
    top: spacing.medium,
    zIndex: 2,
  },
  profileButton: {
    alignItems: 'center',
    backgroundColor: colors.primary,
    borderRadius: 20,
    height: 40,
    justifyContent: 'center',
    width: 40,
  },
  profileMenu: {
    backgroundColor: colors.surface,
    borderColor: colors.surfaceMuted,
    borderRadius: 12,
    borderWidth: 1,
    marginTop: spacing.small,
    minWidth: 140,
    overflow: 'hidden',
    position: 'absolute',
    right: 0,
    top: 40,
  },
  menuItem: {
    minHeight: 48,
    justifyContent: 'center',
    paddingHorizontal: spacing.medium,
  },
  menuLabel: {
    color: colors.text,
    fontSize: 16,
  },
  navigation: {
    borderTopColor: colors.surfaceMuted,
    borderTopWidth: 1,
    flexDirection: 'row',
    paddingBottom: spacing.small,
  },
  navigationItem: {
    alignItems: 'center',
    flex: 1,
    minHeight: 48,
    padding: spacing.small,
  },
  label: {
    color: colors.textMuted,
  },
  activeLabel: {
    color: colors.primary,
    fontWeight: '700',
  },
});
