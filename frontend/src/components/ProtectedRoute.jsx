import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Redirige a login si no hay sesion; con adminOnly, ademas exige rol ADMIN
// (redirige a /home si un CLIENT intenta entrar).
function ProtectedRoute({ children, adminOnly = false }) {
  const { isAuthenticated, isAdmin } = useAuth()

  if (!isAuthenticated) {
    return <Navigate to="/" replace />
  }

  if (adminOnly && !isAdmin) {
    return <Navigate to="/home" replace />
  }

  return children
}

export default ProtectedRoute
