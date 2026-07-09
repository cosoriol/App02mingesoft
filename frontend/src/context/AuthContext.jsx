import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

// Provee el estado de autenticacion simulado (userId + nombre) a toda la app.
// Cuando llegue Keycloak, login()/logout() son los unicos puntos que deberian cambiar.
export function AuthProvider({ children }) {
  const [userId, setUserId] = useState(() => localStorage.getItem('userId'))
  const [userName, setUserName] = useState(() => localStorage.getItem('userName'))

  function login(id, name) {
    localStorage.setItem('userId', id)
    localStorage.setItem('userName', name)
    setUserId(id)
    setUserName(name)
  }

  function logout() {
    localStorage.removeItem('userId')
    localStorage.removeItem('userName')
    setUserId(null)
    setUserName(null)
  }

  const value = { userId, userName, login, logout, isAuthenticated: Boolean(userId) }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider')
  }
  return context
}
