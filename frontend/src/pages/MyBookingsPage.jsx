import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { getMyBookings, cancelBooking, getErrorMessage } from '../services/api.js'

// confirmation-service devuelve el estado ya traducido a texto legible en español
// (ver ConfirmationService.readableStatus), por eso se mapea por esos textos y no por el enum
const STATUS_STYLES = {
  'Pendiente de pago': 'badge-warning',
  Confirmada: 'badge-success',
  Cancelada: 'badge-danger',
  Expirada: 'badge-neutral',
}

function formatPrice(value) {
  return `$${Number(value).toLocaleString('es-CL')}`
}

function MyBookingsPage() {
  const { userId } = useAuth()
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
    getMyBookings(userId)
      .then(setBookings)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  function handleCancel(bookingId) {
    if (!window.confirm('¿Seguro que deseas cancelar esta reserva?')) return

    setCancellingId(bookingId)
    cancelBooking(bookingId, userId)
      .then(loadBookings)
      .catch((err) => window.alert(getErrorMessage(err)))
      .finally(() => setCancellingId(null))
  }

  return (
    <div className="page">
      <Navbar />
      <div className="page-content">
        <h1>Mis reservas</h1>

        {loading && <p className="loading-text">Cargando tus reservas...</p>}
        {error && <div className="alert alert-error">{error}</div>}
        {!loading && !error && bookings.length === 0 && (
          <p className="empty-text">Todavía no tienes reservas.</p>
        )}

        <div className="bookings-list">
          {bookings.map((booking) => {
            // Un cliente solo puede cancelar mientras la reserva sigue PENDING; una vez
            // confirmada y pagada, cancelarla requiere un proceso administrativo aparte
            // (ver ConfirmationService.cancelBooking)
            const canCancel = booking.status === 'Pendiente de pago'
            return (
              <div className="booking-card" key={booking.id}>
                <div className="booking-card-header">
                  <h2>{booking.packageName}</h2>
                  <span className={`badge ${STATUS_STYLES[booking.status] || 'badge-neutral'}`}>
                    {booking.status}
                  </span>
                </div>
                <p className="package-dates">
                  {booking.destination} · {booking.startDate} → {booking.endDate}
                </p>
                <div className="booking-card-details">
                  <span>{booking.passengerCount} pasajeros</span>
                  <span>{formatPrice(booking.totalAmount)}</span>
                </div>

                <div className="booking-card-actions">
                  {booking.status === 'Pendiente de pago' && (
                    <Link to={`/payment/${booking.id}`} className="btn btn-primary">
                      Pagar ahora
                    </Link>
                  )}
                  {booking.status === 'Confirmada' && (
                    <Link to={`/payment-confirmation/${booking.id}`} className="btn btn-outline">
                      Ver comprobante
                    </Link>
                  )}
                  {canCancel && (
                    <button
                      type="button"
                      className="btn btn-danger-outline"
                      onClick={() => handleCancel(booking.id)}
                      disabled={cancellingId === booking.id}
                    >
                      {cancellingId === booking.id ? 'Cancelando...' : 'Cancelar'}
                    </button>
                  )}
                  {booking.status === 'Cancelada' && <span className="text-muted">Reserva cancelada</span>}
                  {booking.status === 'Expirada' && <span className="text-muted">Reserva expirada</span>}
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}

export default MyBookingsPage
