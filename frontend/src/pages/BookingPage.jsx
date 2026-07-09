import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { getPackageById, createBooking, getErrorMessage } from '../services/api.js'

// Reglas de descuento por grupo espejadas de booking-service/DiscountConfig (valores por
// defecto), solo para mostrar una estimacion instantanea. "Cliente frecuente" y "Multi-paquete"
// dependen del historial del usuario en la base de datos y no se pueden calcular en el
// frontend: el monto definitivo con todos los descuentos se confirma al crear la reserva.
const GROUP_THRESHOLD = 4
const GROUP_DISCOUNT_PCT = 5

function formatPrice(value) {
  return `$${Number(value).toLocaleString('es-CL')}`
}

function BookingPage() {
  const { packageId } = useParams()
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { userId } = useAuth()

  const [pkg, setPkg] = useState(null)
  const [passengerCount, setPassengerCount] = useState(() => Number(searchParams.get('passengers')) || 1)
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    getPackageById(packageId)
      .then(setPkg)
      .catch((err) => setLoadError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [packageId])

  const baseAmount = useMemo(() => {
    if (!pkg) return 0
    return Number(pkg.price) * passengerCount
  }, [pkg, passengerCount])

  const groupDiscountApplies = passengerCount >= GROUP_THRESHOLD
  const estimatedDiscount = groupDiscountApplies ? (baseAmount * GROUP_DISCOUNT_PCT) / 100 : 0
  const estimatedTotal = baseAmount - estimatedDiscount

  function handleSubmit(event) {
    event.preventDefault()
    setError('')

    if (!pkg) return

    const availableSlots = pkg.availableSlots ?? pkg.totalSlots - pkg.bookedSlots
    if (passengerCount > availableSlots) {
      setError(`Solo hay ${availableSlots} cupos disponibles para este paquete`)
      return
    }

    setSubmitting(true)
    createBooking(userId, { packageId: Number(packageId), passengerCount })
      .then((booking) => navigate(`/payment/${booking.id}`))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setSubmitting(false))
  }

  return (
    <div className="page">
      <Navbar />
      <div className="page-content booking-page">
        {loading && <p className="loading-text">Cargando datos del paquete...</p>}
        {loadError && <div className="alert alert-error">{loadError}</div>}

        {!loading && !loadError && pkg && (
          <form className="booking-summary-card" onSubmit={handleSubmit}>
            <h1>Reservar {pkg.name}</h1>
            <p className="package-dates">
              {pkg.destination} · {pkg.startDate} → {pkg.endDate}
            </p>

            <label className="form-field">
              <span>Cantidad de pasajeros</span>
              <input
                type="number"
                min="1"
                value={passengerCount}
                onChange={(e) => setPassengerCount(Math.max(1, Number(e.target.value)))}
              />
            </label>

            <div className="price-breakdown">
              <div className="price-row">
                <span>
                  Precio base: {formatPrice(pkg.price)} × {passengerCount} pasajeros
                </span>
                <span>{formatPrice(baseAmount)}</span>
              </div>

              <div className="discount-list">
                <p className="discount-title">Descuentos aplicables</p>
                <div className={`discount-row ${groupDiscountApplies ? 'discount-active' : 'discount-inactive'}`}>
                  <span>{groupDiscountApplies ? '✓' : '○'} Descuento por grupo (4+): 5%</span>
                </div>
                <div className="discount-row discount-pending">
                  <span>○ Cliente frecuente: 10% (se verifica al confirmar)</span>
                </div>
                <div className="discount-row discount-pending">
                  <span>○ Multi-paquete: 3% (se verifica al confirmar)</span>
                </div>
              </div>

              <div className="price-row price-total">
                <span>Monto total estimado</span>
                <span>{formatPrice(estimatedTotal)}</span>
              </div>
              <p className="price-note">
                El total final, con todos los descuentos que apliquen según tu historial, se confirmará al crear
                la reserva.
              </p>
            </div>

            {error && <div className="alert alert-error">{error}</div>}

            <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
              {submitting ? 'Procesando...' : 'Continuar a reserva'}
            </button>
          </form>
        )}
      </div>
    </div>
  )
}

export default BookingPage
