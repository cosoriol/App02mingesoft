import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register as registerRequest, getErrorMessage } from '../services/api.js'

// Reglas de contraseña fuerte, evaluadas en vivo mientras el usuario escribe.
// Deben coincidir exactamente con las que valida user-service (UserService.validatePasswordStrength).
const PASSWORD_RULES = [
  { test: (pw) => pw.length >= 8, label: 'Al menos 8 caracteres' },
  { test: (pw) => /[A-Z]/.test(pw), label: 'Una letra mayúscula' },
  { test: (pw) => /[a-z]/.test(pw), label: 'Una letra minúscula' },
  { test: (pw) => /\d/.test(pw), label: 'Un número' },
  { test: (pw) => /[@#$%^&+=!*]/.test(pw), label: 'Un carácter especial (@#$%^&+=!*)' },
]

// Pagina de registro. Crea la cuenta en user-service (Epica 1); no inicia sesion
// automaticamente, redirige a login para que el usuario entre con su propia contrasena.
function RegisterPage() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const navigate = useNavigate()

  const passwordValid = PASSWORD_RULES.every((rule) => rule.test(password))

  function handleSubmit(event) {
    event.preventDefault()
    setError('')

    if (!name.trim() || !email.trim()) {
      setError('Nombre y email son obligatorios')
      return
    }
    if (!passwordValid) {
      setError('La contraseña no cumple los requisitos mínimos de seguridad')
      return
    }
    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }

    setLoading(true)
    registerRequest({ fullName: name.trim(), email: email.trim(), phone, password })
      .then(() => navigate('/', { state: { registered: true } }))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Crear cuenta</h1>
        <p className="auth-subtitle">Regístrate para reservar tu próximo viaje</p>

        {error && <div className="alert alert-error">{error}</div>}

        <label className="form-field">
          <span>Nombre completo</span>
          <input type="text" value={name} onChange={(e) => setName(e.target.value)} required />
        </label>

        <label className="form-field">
          <span>Email</span>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>

        <label className="form-field">
          <span>Teléfono (opcional)</span>
          <input type="tel" value={phone} onChange={(e) => setPhone(e.target.value)} />
        </label>

        <label className="form-field">
          <span>Contraseña</span>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </label>

        {password && (
          <ul className="password-rules">
            {PASSWORD_RULES.map((rule) => (
              <li key={rule.label} className={rule.test(password) ? 'rule-ok' : 'rule-pending'}>
                {rule.test(password) ? '✓' : '○'} {rule.label}
              </li>
            ))}
          </ul>
        )}

        <label className="form-field">
          <span>Confirmar contraseña</span>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </label>

        <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
          {loading ? 'Creando cuenta...' : 'Registrarse'}
        </button>

        <p className="auth-footer">
          ¿Ya tienes cuenta? <Link to="/">Inicia sesión</Link>
        </p>
      </form>
    </div>
  )
}

export default RegisterPage
