import { env } from '../config/env';

type ApiRequest = {
  body?: unknown;
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  token?: string;
};

export class ApiError extends Error {
  constructor(
    readonly status: number,
    message: string,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export async function apiClient<T>(path: string, request: ApiRequest = {}): Promise<T> {
  const headers: Record<string, string> = { Accept: 'application/json' };

  if (request.body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }
  if (request.token) {
    headers.Authorization = `Bearer ${request.token}`;
  }

  const response = await fetch(`${env.apiBaseUrl}${path}`, {
    ...(request.body !== undefined ? { body: JSON.stringify(request.body) } : {}),
    headers,
    method: request.method ?? 'GET',
  });

  if (!response.ok) {
    throw new ApiError(response.status, await readErrorMessage(response));
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

async function readErrorMessage(response: Response) {
  try {
    const payload = (await response.json()) as { message?: string; error?: string };
    return payload.message ?? payload.error ?? `Request failed (${response.status})`;
  } catch {
    return `Request failed (${response.status})`;
  }
}
