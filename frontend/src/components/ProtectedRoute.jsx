import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Redirige a login si no hay un usuario (simulado) en sesion
function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    return <Navigate to="/" replace />
  }

  return children
}

export default ProtectedRoute
