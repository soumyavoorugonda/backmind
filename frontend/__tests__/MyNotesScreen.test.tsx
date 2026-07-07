import { render } from '@testing-library/react-native';

import { MyNotesScreen } from '../src/features/notes/MyNotesScreen';

describe('MyNotesScreen', () => {
  it('displays every note written by the user', () => {
    const screen = render(
      <MyNotesScreen
        notes={[
          { id: 'note-1', content: 'First memory', category: 'Learning', createdAt: '2026-07-07T12:00:00Z' },
          { id: 'note-2', content: 'Second memory', category: null, createdAt: '2026-07-08T12:00:00Z' },
        ]}
      />,
    );

    expect(screen.getByRole('header', { name: 'My notes' })).toBeTruthy();
    expect(screen.getByText('First memory')).toBeTruthy();
    expect(screen.getByText('Second memory')).toBeTruthy();
    expect(screen.getByText('Learning')).toBeTruthy();
  });
});
