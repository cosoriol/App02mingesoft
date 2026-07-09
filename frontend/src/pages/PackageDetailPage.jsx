import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import { getPackageById, getErrorMessage } from '../services/api.js'

function formatPrice(value) {
  return `$${Number(value).toLocaleString('es-CL')}`
}

// Detalle completo de un paquete turistico, con seleccion de pasajeros
function PackageDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [pkg, setPkg] = useState(null)
  const [passengerCount, setPassengerCount] = useState(1)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    setLoading(true)
    getPackageById(id)
      .then(setPkg)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [id])

  function handleContinue(event) {
    event.preventDefault()
    navigate(`/booking/${id}?passengers=${passengerCount}`)
  }

  const availableSlots = pkg ? pkg.availableSlots ?? pkg.totalSlots - pkg.bookedSlots : 0

  return (
    <div className="page">
      <Navbar />
      <div className="page-content detail-page">
        {loading && <p className="loading-text">Cargando paquete...</p>}
        {error && <div className="alert alert-error">{error}</div>}

        {!loading && !error && pkg && (
          <>
            <button type="button" className="btn-link" onClick={() => navigate('/home')}>
              ← Volver a paquetes
            </button>

            <h1>{pkg.name}</h1>
            <span className="badge badge-destination">{pkg.destination}</span>

            <p className="detail-description">{pkg.description}</p>

            <div className="detail-grid">
              <div className="detail-item">
                <span className="detail-label">Fechas</span>
                <span>
                  {pkg.startDate} → {pkg.endDate}
                </span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Duración</span>
                <span>{pkg.durationDays} días</span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Precio por persona</span>
                <span className="detail-price">{formatPrice(pkg.price)}</span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Cupos disponibles</span>
                <span>{availableSlots}</span>
              </div>
            </div>

            {pkg.includedServices && (
              <div className="detail-section">
                <h3>Servicios incluidos</h3>
                <p>{pkg.includedServices}</p>
              </div>
            )}

            {pkg.restrictions && (
              <div className="detail-section">
                <h3>Restricciones</h3>
                <p>{pkg.restrictions}</p>
              </div>
            )}

            <form className="detail-booking-box" onSubmit={handleContinue}>
              <label className="form-field">
                <span>Cantidad de pasajeros</span>
                <input
                  type="number"
                  min="1"
                  max={availableSlots || undefined}
                  value={passengerCount}
                  onChange={(e) => setPassengerCount(Math.max(1, Number(e.target.value)))}
                />
              </label>
              <button type="submit" className="btn btn-primary btn-block" disabled={availableSlots <= 0}>
                {availableSlots <= 0 ? 'Sin cupos disponibles' : 'Continuar a reserva'}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  )
}

export default PackageDetailPage
