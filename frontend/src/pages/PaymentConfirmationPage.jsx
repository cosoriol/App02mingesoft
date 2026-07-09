import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import { getPaymentVoucher, getErrorMessage } from '../services/api.js'

function formatPrice(value) {
  return `$${Number(value).toLocaleString('es-CL')}`
}

function PaymentConfirmationPage() {
  const { bookingId } = useParams()
  const [voucher, setVoucher] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getPaymentVoucher(bookingId)
      .then(setVoucher)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [bookingId])

  return (
    <div className="page">
      <Navbar />
      <div className="page-content confirmation-page">
        {loading && <p className="loading-text">Cargando comprobante...</p>}
        {error && <div className="alert alert-error">{error}</div>}

        {!loading && !error && voucher && (
          <div className="confirmation-card">
            <div className="confirmation-check">✓</div>
            <h1>¡Pago realizado exitosamente!</h1>
            <p className="confirmation-voucher-number">Reserva #{voucher.voucherNumber}</p>

            <div className="confirmation-details">
              <div className="detail-item">
                <span className="detail-label">Paquete</span>
                <span>{voucher.packageName}</span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Destino</span>
                <span>{voucher.destination}</span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Fechas</span>
                <span>
                  {voucher.startDate} → {voucher.endDate}
                </span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Pasajeros</span>
                <span>{voucher.passengerCount}</span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Monto pagado</span>
                <span>{formatPrice(voucher.totalAmount)}</span>
              </div>
              {voucher.paymentInfo && (
                <div className="detail-item">
                  <span className="detail-label">Método de pago</span>
                  <span>Tarjeta de crédito **** {voucher.paymentInfo.cardLastFour}</span>
                </div>
              )}
            </div>

            <div className="confirmation-actions">
              <Link to="/my-bookings" className="btn btn-primary">
                Ver mis reservas
              </Link>
              <Link to="/home" className="btn btn-outline">
                Buscar más paquetes
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default PaymentConfirmationPage
