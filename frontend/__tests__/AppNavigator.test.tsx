import { fireEvent, render } from '@testing-library/react-native';
import { Ionicons } from '@expo/vector-icons';
import { StyleSheet } from 'react-native';

jest.mock('../src/features/notes/noteApi', () => ({
  noteApi: { create: jest.fn(), findAll: jest.fn(), review: jest.fn(), today: jest.fn() },
}));

import { AppNavigator } from '../src/navigation/AppNavigator';

describe('AppNavigator', () => {
  it('navigates between every Phase 3 screen', () => {
    const onLogout = jest.fn();
    const screen = render(<AppNavigator onLogout={onLogout} />);

    expect(screen.getByRole('header', { name: 'Today' })).toBeTruthy();
    expect(screen.queryByText('👤')).toBeNull();
    expect(screen.UNSAFE_getByType(Ionicons).props).toMatchObject({
      name: 'person-outline',
      size: 20,
    });
    expect(
      StyleSheet.flatten(screen.getByRole('button', { name: 'Open profile menu' }).props.style),
    ).toMatchObject({ borderRadius: 20, height: 40, width: 40 });

    fireEvent.press(screen.getByRole('button', { name: 'My notes' }));
    expect(screen.getByRole('header', { name: 'My notes' })).toBeTruthy();

    fireEvent.press(screen.getByRole('button', { name: 'New note' }));
    expect(screen.getByRole('header', { name: 'New note' })).toBeTruthy();

    fireEvent.press(screen.getByRole('button', { name: 'Open profile menu' }));
    expect(screen.queryByRole('button', { name: 'Settings' })).toBeNull();
    fireEvent.press(screen.getByRole('button', { name: 'Log out' }));
    expect(onLogout).toHaveBeenCalledTimes(1);
  });

  it('closes the profile dropdown when the pointer leaves it', () => {
    const screen = render(<AppNavigator onLogout={jest.fn()} />);

    fireEvent.press(screen.getByRole('button', { name: 'Open profile menu' }));
    expect(screen.getByRole('button', { name: 'Log out' })).toBeTruthy();

    fireEvent(screen.getByTestId('profile-menu-area'), 'hoverOut');
    expect(screen.queryByRole('button', { name: 'Log out' })).toBeNull();
  });
});
