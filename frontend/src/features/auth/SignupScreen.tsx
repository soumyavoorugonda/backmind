import { Credentials, CredentialsForm } from './CredentialsForm';

export type SignupCredentials = Credentials;

type SignupScreenProps = {
  error?: string | null;
  loading?: boolean;
  onSubmit: (credentials: SignupCredentials) => void;
};

export function SignupScreen({ error, loading, onSubmit }: SignupScreenProps) {
  return (
    <CredentialsForm
      actionLabel="Create account"
      error={error}
      loading={loading}
      onSubmit={onSubmit}
      passwordAutoComplete="new-password"
      title="Create your account"
    />
  );
}
