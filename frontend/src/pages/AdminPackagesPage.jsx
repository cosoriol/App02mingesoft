import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import {
  getAllPackagesAdmin,
  createPackage,
  updatePackage,
  changePackageStatus,
  getErrorMessage,
} from '../services/api.js'

const STATUS_BADGES = {
  AVAILABLE: 'badge-success',
  SOLD_OUT: 'badge-warning',
  EXPIRED: 'badge-neutral',
  CANCELLED: 'badge-danger',
}

const EMPTY_FORM = {
  name: '',
  destination: '',
  description: '',
  startDate: '',
  endDate: '',
  price: '',
  totalSlots: '',
  includedServices: '',
  restrictions: '',
  travelType: '',
  season: '',
}

function formatPrice(value) {
  return `$${Number(value ?? 0).toLocaleString('es-CL')}`
}

// Administracion de paquetes turisticos (Epica 2). Acceso exclusivo ADMIN (ver
// ProtectedRoute adminOnly en App.jsx); package-service tambien revalida el rol.
function AdminPackagesPage() {
  const { role } = useAuth()
  const [packages, setPackages] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formError, setFormError] = useState('')
  const [form, setForm] = useState(EMPTY_FORM)
  const [saving, setSaving] = useState(false)
  const [editingId, setEditingId] = useState(null)

  useEffect(() => {
    loadPackages()
  }, [])

  function loadPackages() {
    setLoading(true)
    setError('')
    getAllPackagesAdmin(role)
      .then(setPackages)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  function handleFieldChange(field) {
    return (event) => setForm((prev) => ({ ...prev, [field]: event.target.value }))
  }

  function handleEditClick(pkg) {
    setEditingId(pkg.id)
    setFormError('')
    setForm({
      name: pkg.name,
      destination: pkg.destination,
      description: pkg.description,
      startDate: pkg.startDate,
      endDate: pkg.endDate,
      price: pkg.price,
      totalSlots: pkg.totalSlots,
      includedServices: pkg.includedServices || '',
      restrictions: pkg.restrictions || '',
      travelType: pkg.travelType || '',
      season: pkg.season || '',
    })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function handleCancelEdit() {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setFormError('')
  }

  function handleSubmit(event) {
    event.preventDefault()
    setFormError('')
    setSaving(true)

    const payload = { ...form, price: Number(form.price), totalSlots: Number(form.totalSlots) }
    const request = editingId ? updatePackage(editingId, role, payload) : createPackage(role, payload)

    request
      .then(() => {
        setForm(EMPTY_FORM)
        setEditingId(null)
        loadPackages()
      })
      .catch((err) => setFormError(getErrorMessage(err)))
      .finally(() => setSaving(false))
  }

  function handleToggleStatus(pkg) {
    const newStatus = pkg.status === 'CANCELLED' ? 'AVAILABLE' : 'CANCELLED'
    changePackageStatus(pkg.id, newStatus, role)
      .then(loadPackages)
      .catch((err) => setError(getErrorMessage(err)))
  }

  return (
    <div className="page">
      <Navbar />
      <div className="page-content">
        <h1>Administrar paquetes</h1>

        <section className="report-section">
          <h2>{editingId ? `Editar paquete #${editingId}` : 'Crear nuevo paquete'}</h2>
          {formError && <div className="alert alert-error">{formError}</div>}
          <form onSubmit={handleSubmit}>
            <div className="admin-form-grid">
              <label className="form-field">
                <span>Nombre</span>
                <input value={form.name} onChange={handleFieldChange('name')} required />
              </label>
              <label className="form-field">
                <span>Destino</span>
                <input value={form.destination} onChange={handleFieldChange('destination')} required />
              </label>
              <label className="form-field">
                <span>Fecha inicio</span>
                <input type="date" value={form.startDate} onChange={handleFieldChange('startDate')} required />
              </label>
              <label className="form-field">
                <span>Fecha término</span>
                <input type="date" value={form.endDate} onChange={handleFieldChange('endDate')} required />
              </label>
              <label className="form-field">
                <span>Precio (por persona)</span>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  value={form.price}
                  onChange={handleFieldChange('price')}
                  required
                />
              </label>
              <label className="form-field">
                <span>Cupos totales</span>
                <input
                  type="number"
                  min="1"
                  value={form.totalSlots}
                  onChange={handleFieldChange('totalSlots')}
                  required
                />
              </label>
              <label className="form-field">
                <span>Tipo de viaje</span>
                <input
                  placeholder="Aventura, Playa, Cultural..."
                  value={form.travelType}
                  onChange={handleFieldChange('travelType')}
                  required
                />
              </label>
              <label className="form-field">
                <span>Temporada</span>
                <input
                  placeholder="Alta, Media, Baja"
                  value={form.season}
                  onChange={handleFieldChange('season')}
                  required
                />
              </label>
            </div>

            <label className="form-field" style={{ marginTop: '1rem' }}>
              <span>Descripción</span>
              <textarea value={form.description} onChange={handleFieldChange('description')} rows={3} required />
            </label>

            <div className="admin-form-grid" style={{ marginTop: '1rem' }}>
              <label className="form-field">
                <span>Servicios incluidos (opcional)</span>
                <input value={form.includedServices} onChange={handleFieldChange('includedServices')} />
              </label>
              <label className="form-field">
                <span>Restricciones (opcional)</span>
                <input value={form.restrictions} onChange={handleFieldChange('restrictions')} />
              </label>
            </div>

            <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1rem' }}>
              <button type="submit" className="btn btn-primary" disabled={saving}>
                {saving ? 'Guardando...' : editingId ? 'Guardar cambios' : 'Crear paquete'}
              </button>
              {editingId && (
                <button type="button" className="btn btn-outline" onClick={handleCancelEdit}>
                  Cancelar edición
                </button>
              )}
            </div>
          </form>
        </section>

        <section className="report-section">
          <h2>Todos los paquetes ({packages.length})</h2>
          {error && <div className="alert alert-error">{error}</div>}
          {loading && <p className="loading-text">Cargando paquetes...</p>}

          <div className="package-grid">
            {packages.map((pkg) => (
              <div className="package-admin-card" key={pkg.id}>
                <div className="package-card-header">
                  <h3>{pkg.name}</h3>
                  <span className={`badge ${STATUS_BADGES[pkg.status] || 'badge-neutral'}`}>{pkg.status}</span>
                </div>
                <p className="package-dates">📍 {pkg.destination}</p>
                <p className="package-price">{formatPrice(pkg.price)}</p>
                <p className="package-slots">
                  {pkg.availableSlots}/{pkg.totalSlots} cupos disponibles
                  {(pkg.travelType || pkg.season) && ` · ${pkg.travelType || ''} ${pkg.season || ''}`.trim()}
                </p>
                <div className="package-admin-actions">
                  <button type="button" className="btn btn-outline btn-small" onClick={() => handleEditClick(pkg)}>
                    Editar
                  </button>
                  <button
                    type="button"
                    className="btn btn-danger-outline btn-small"
                    onClick={() => handleToggleStatus(pkg)}
                  >
                    {pkg.status === 'CANCELLED' ? 'Reactivar' : 'Cancelar'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  )
}

export default AdminPackagesPage
