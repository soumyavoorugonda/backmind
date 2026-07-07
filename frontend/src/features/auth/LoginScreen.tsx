import { Credentials, CredentialsForm } from './CredentialsForm';

export type LoginCredentials = Credentials;

type LoginScreenProps = {
  error?: string | null;
  loading?: boolean;
  onSubmit: (credentials: LoginCredentials) => void;
};

export function LoginScreen({ error, loading, onSubmit }: LoginScreenProps) {
  return (
    <CredentialsForm
      actionLabel="Log in"
      error={error}
      loading={loading}
      onSubmit={onSubmit}
      passwordAutoComplete="current-password"
      title="Welcome back"
    />
  );
}
