import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { getAllBookingsAdmin, cancelBooking, getErrorMessage } from '../services/api.js'

const STATUS_BADGES = {
  'Pendiente de pago': 'badge-warning',
  Confirmada: 'badge-success',
  Cancelada: 'badge-danger',
  Expirada: 'badge-neutral',
}

function formatPrice(value) {
  return `$${Number(value ?? 0).toLocaleString('es-CL')}`
}

// Administracion de reservas (Epica 6): todas las reservas de todos los clientes, no solo
// las propias (ver MyBookingsPage). Acceso exclusivo ADMIN, revalidado en confirmation-service.
function AdminBookingsPage() {
  const { userId, role } = useAuth()
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [cancellingId, setCancellingId] = useState(null)

  useEffect(() => {
    loadBookings()
  }, [])

  function loadBookings() {
    setLoading(true)
    setError('')
    getAllBookingsAdmin(role)
      .then(setBookings)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  function handleCancel(bookingId) {
    if (!window.confirm('¿Cancelar esta reserva?')) return
    setCancellingId(bookingId)
    cancelBooking(bookingId, userId, role)
      .then(loadBookings)
      .catch((err) => window.alert(getErrorMessage(err)))
      .finally(() => setCancellingId(null))
  }

  return (
    <div className="page">
      <Navbar />
      <div className="page-content">
        <h1>Administrar reservas</h1>

        {error && <div className="alert alert-error">{error}</div>}
        {loading && <p className="loading-text">Cargando reservas...</p>}
        {!loading && !error && bookings.length === 0 && <p className="empty-text">No hay reservas.</p>}

        {bookings.length > 0 && (
          <div className="report-table-wrapper">
            <table className="report-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Cliente</th>
                  <th>Paquete</th>
                  <th>Destino</th>
                  <th>Pasajeros</th>
                  <th>Total</th>
                  <th>Estado</th>
                  <th>Creada</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {bookings.map((b) => {
                  const canCancel = b.status === 'Pendiente de pago' || b.status === 'Confirmada'
                  return (
                    <tr key={b.id}>
                      <td>{b.id}</td>
                      <td>{b.userId}</td>
                      <td>{b.packageName}</td>
                      <td>{b.destination}</td>
                      <td>{b.passengerCount}</td>
                      <td>{formatPrice(b.totalAmount)}</td>
                      <td>
                        <span className={`badge ${STATUS_BADGES[b.status] || 'badge-neutral'}`}>{b.status}</span>
                      </td>
                      <td>{b.createdAt?.slice(0, 10)}</td>
                      <td>
                        {canCancel && (
                          <button
                            type="button"
                            className="btn btn-danger-outline btn-small"
                            onClick={() => handleCancel(b.id)}
                            disabled={cancellingId === b.id}
                          >
                            {cancellingId === b.id ? 'Cancelando...' : 'Cancelar'}
                          </button>
                        )}
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminBookingsPage
