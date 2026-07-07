import { Button } from '../../components/ui/Button';
import { useAuth } from './AuthContext';

export function LogoutButton() {
  const { signOut } = useAuth();
  return <Button label="Log out" onPress={() => void signOut()} />;
}
