import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { authApi, restoreSession, type UserProfile } from '../lib/api'
type AuthContextValue = { user: UserProfile | null; loading: boolean; login: (email: string, password: string) => Promise<void>; register: (displayName: string, email: string, password: string) => Promise<void>; logout: () => Promise<void> }
const AuthContext = createContext<AuthContextValue | null>(null)
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  useEffect(() => { let active = true; restoreSession().then(value => { if (active) setUser(value) }).catch(() => { if (active) setUser(null) }).finally(() => { if (active) setLoading(false) }); return () => { active = false } }, [])
  const value = useMemo<AuthContextValue>(() => ({ user, loading,
    async login(email, password) { setUser(await authApi.login({ email, password })) },
    async register(displayName, email, password) { setUser(await authApi.register({ displayName, email, password })) },
    async logout() { try { await authApi.logout() } catch { /* clear local session even if the server is unreachable */ } finally { setUser(null) } },
  }), [user, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
export function useAuth() { const value = useContext(AuthContext); if (!value) throw new Error('useAuth must be used within AuthProvider'); return value }
