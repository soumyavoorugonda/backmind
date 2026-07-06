import { render } from '@testing-library/react-native';

import { RootNavigator } from '../src/navigation/RootNavigator';

describe('RootNavigator', () => {
  it('renders the BackMind landing route', async () => {
    const screen = render(<RootNavigator />);

    expect(await screen.findByRole('header', { name: 'BackMind' })).toBeTruthy();
  });
});
