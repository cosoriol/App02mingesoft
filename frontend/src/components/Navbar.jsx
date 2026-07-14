import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Barra de navegacion superior, visible en todas las paginas autenticadas
function Navbar() {
  const { userName, logout, isAuthenticated, isAdmin } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <nav className="navbar">
      <Link to="/home" className="navbar-logo">
        ✈ TravelAgency
      </Link>

      {isAuthenticated && (
        <div className="navbar-links">
          <Link to="/home">Paquetes</Link>
          <Link to="/my-bookings">Mis reservas</Link>
          {isAdmin && <Link to="/reports">Reportes</Link>}
          <span className="navbar-user">Bienvenido, {userName || 'Usuario'}</span>
          <button type="button" className="btn btn-outline btn-small" onClick={handleLogout}>
            Cerrar sesión
          </button>
        </div>
      )}
    </nav>
  )
}

export default Navbar
