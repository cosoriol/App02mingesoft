import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

// Provee el estado de autenticacion (userId + nombre + rol) a toda la app, respaldado por
// user-service. Cuando llegue Keycloak, login()/logout() son los unicos puntos que deberian cambiar.
export function AuthProvider({ children }) {
  const [userId, setUserId] = useState(() => localStorage.getItem('userId'))
  const [userName, setUserName] = useState(() => localStorage.getItem('userName'))
  const [role, setRole] = useState(() => localStorage.getItem('role'))

  function login(id, name, userRole) {
    localStorage.setItem('userId', id)
    localStorage.setItem('userName', name)
    localStorage.setItem('role', userRole)
    setUserId(id)
    setUserName(name)
    setRole(userRole)
  }

  function logout() {
    localStorage.removeItem('userId')
    localStorage.removeItem('userName')
    localStorage.removeItem('role')
    setUserId(null)
    setUserName(null)
    setRole(null)
  }

  const value = {
    userId,
    userName,
    role,
    login,
    logout,
    isAuthenticated: Boolean(userId),
    isAdmin: role === 'ADMIN',
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider')
  }
  return context
}
