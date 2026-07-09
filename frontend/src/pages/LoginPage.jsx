import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Pagina de inicio de sesion. No hay backend de autenticacion todavia (llegara con
// Keycloak): por ahora se simula el login guardando un userId fijo en localStorage.
function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()

  function handleSubmit(event) {
    event.preventDefault()
    setError('')

    if (!email.trim() || !password) {
      setError('Ingresa tu email y contraseña')
      return
    }

    setLoading(true)
    setTimeout(() => {
      login('1', email.split('@')[0])
      setLoading(false)
      navigate('/home')
    }, 300)
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>TravelAgency</h1>
        <p className="auth-subtitle">Inicia sesión para continuar</p>

        {error && <div className="alert alert-error">{error}</div>}

        <label className="form-field">
          <span>Email</span>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="tu@email.com"
            required
          />
        </label>

        <label className="form-field">
          <span>Contraseña</span>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
            required
          />
        </label>

        <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
          {loading ? 'Ingresando...' : 'Iniciar sesión'}
        </button>

        <p className="auth-footer">
          ¿No tienes cuenta? <Link to="/register">Regístrate</Link>
        </p>
      </form>
    </div>
  )
}

export default LoginPage
