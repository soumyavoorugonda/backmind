import { StatusBar } from 'expo-status-bar';

import { AuthProvider } from './src/features/auth/AuthContext';
import { SessionNavigator } from './src/navigation/SessionNavigator';

export default function App() {
  return (
    <>
      <AuthProvider>
        <SessionNavigator />
      </AuthProvider>
      <StatusBar style="auto" />
    </>
  );
}
