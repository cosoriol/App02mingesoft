import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { login as loginRequest, getErrorMessage } from '../services/api.js'

// Pagina de inicio de sesion. Valida email/contrasena contra user-service (Epica 1).
function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setLoading(true)

    loginRequest(email, password)
      .then((data) => {
        login(String(data.user.id), data.user.fullName, data.user.role)
        navigate('/home')
      })
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>TravelAgency</h1>
        <p className="auth-subtitle">Inicia sesión para continuar</p>

        {location.state?.registered && (
          <div className="alert alert-success">Cuenta creada. Ya puedes iniciar sesión.</div>
        )}
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
