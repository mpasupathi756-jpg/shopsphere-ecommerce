const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1'
let accessToken: string | null = null
let csrfToken: string | null = null
export type UserProfile = { id: string; email: string; displayName: string; roles: string[]; emailVerified: boolean }
export type AuthResponse = { accessToken: string; tokenType: string; expiresIn: number; user: UserProfile }
export function setAccessToken(token: string | null) { accessToken = token }
async function ensureCsrf(): Promise<string> {
  if (csrfToken) return csrfToken
  const response = await fetch(`${API_BASE}/auth/csrf`, { credentials: 'include' })
  if (!response.ok) throw new Error('Unable to start a secure session. Please retry.')
  const data = await response.json() as { csrfToken: string }
  csrfToken = data.csrfToken
  return csrfToken
}
async function send<T>(path: string, options: RequestInit = {}, retry = true): Promise<T> {
  const method = (options.method ?? 'GET').toUpperCase()
  const headers = new Headers(options.headers)
  if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json')
  if (accessToken) headers.set('Authorization', `Bearer ${accessToken}`)
  if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) headers.set('X-CSRF-TOKEN', await ensureCsrf())
  const response = await fetch(`${API_BASE}${path}`, { ...options, method, headers, credentials: 'include' })
  if (response.status === 401 && retry && !path.startsWith('/auth/')) {
    try { await restoreSession(); return send<T>(path, options, false) } catch { setAccessToken(null) }
  }
  if (!response.ok) {
    let message = 'Something went wrong. Please try again.'
    try { const data = await response.json() as { detail?: string; message?: string }; message = data.detail ?? data.message ?? message } catch { /* use fallback */ }
    throw new Error(message)
  }
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}
export async function restoreSession(): Promise<UserProfile> {
  const csrf = await ensureCsrf()
  const response = await fetch(`${API_BASE}/auth/refresh`, { method: 'POST', credentials: 'include', headers: { 'X-CSRF-TOKEN': csrf, 'Content-Type': 'application/json' }, body: '{}' })
  if (!response.ok) { setAccessToken(null); throw new Error('No active session') }
  const data = await response.json() as AuthResponse
  setAccessToken(data.accessToken)
  return data.user
}
export const authApi = {
  async register(input: { displayName: string; email: string; password: string }) { const data = await send<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(input) }, false); setAccessToken(data.accessToken); return data.user },
  async login(input: { email: string; password: string }) { const data = await send<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(input) }, false); setAccessToken(data.accessToken); return data.user },
  async logout() { try { await send<void>('/auth/logout', { method: 'POST', body: '{}' }, false) } finally { setAccessToken(null); csrfToken = null } },
  async me() { return send<UserProfile>('/auth/me') },
}
