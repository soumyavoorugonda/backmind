import { fireEvent, render } from '@testing-library/react-native';
import { Ionicons } from '@expo/vector-icons';
import { StyleSheet } from 'react-native';

import { HomeFeedScreen } from '../src/features/feed/HomeFeedScreen';
import { colors } from '../src/theme/tokens';

describe('HomeFeedScreen', () => {
  it('renders a resurfaced note and submits review actions', () => {
    const onReview = jest.fn();
    const screen = render(
      <HomeFeedScreen
        notes={[
          {
            id: 'note-1',
            content: 'Diversification does not remove market risk.',
            category: 'You learned this 10 days ago',
            createdAt: '2026-07-07T12:00:00Z',
          },
        ]}
        onReview={onReview}
      />,
    );

    expect(screen.getByText('Diversification does not remove market risk.')).toBeTruthy();
    expect(screen.getByText('You learned this 10 days ago')).toBeTruthy();
    expect(StyleSheet.flatten(screen.getByText('You learned this 10 days ago').props.style)).toMatchObject({
      fontSize: 16,
    });
    expect(screen.getByText('Jul 7')).toBeTruthy();
    expect(screen.getByText('Y')).toBeTruthy();
    expect(screen.queryByText('👍')).toBeNull();
    expect(screen.queryByText('👎')).toBeNull();
    expect(screen.queryByText('🤲')).toBeNull();
    expect(screen.queryByText('❓')).toBeNull();
    expect(screen.UNSAFE_getAllByType(Ionicons).map((icon) => icon.props.name)).toEqual([
      'thumbs-up-outline',
      'thumbs-down-outline',
      'heart-outline',
      'help-outline',
    ]);
    expect(StyleSheet.flatten(screen.getByTestId('resurfacing-note-card-note-1').props.style)).toMatchObject({
      backgroundColor: colors.surfaceMuted,
      borderRadius: 10,
      borderWidth: 0,
    });
    expect(StyleSheet.flatten(screen.getByRole('button', { name: 'Useful' }).props.style)).toMatchObject({
      backgroundColor: 'transparent',
      borderWidth: 0,
    });

    fireEvent.press(screen.getByRole('button', { name: 'Useful' }));
    fireEvent.press(screen.getByRole('button', { name: 'Not useful' }));
    fireEvent.press(screen.getByRole('button', { name: 'Still believe this' }));
    fireEvent.press(screen.getByRole('button', { name: 'I forgot this' }));

    expect(onReview).toHaveBeenNthCalledWith(1, 'note-1', 'USEFUL');
    expect(onReview).toHaveBeenNthCalledWith(2, 'note-1', 'NOT_USEFUL');
    expect(onReview).toHaveBeenNthCalledWith(3, 'note-1', 'STILL_BELIEVE');
    expect(onReview).toHaveBeenNthCalledWith(4, 'note-1', 'FORGOT_THIS');
  });

  it('renders a divider between note cards', () => {
    const screen = render(
      <HomeFeedScreen
        notes={[
          { id: 'note-1', content: 'First note', category: 'Health' },
          { id: 'note-2', content: 'Second note', category: 'Learning' },
        ]}
        onReview={jest.fn()}
      />,
    );

    expect(StyleSheet.flatten(screen.getByTestId('feed-card-separator-1').props.style)).toMatchObject({
      marginHorizontal: -16,
    });
    expect(screen.queryByTestId('feed-card-separator-2')).toBeNull();
  });
});
