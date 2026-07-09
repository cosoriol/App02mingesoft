import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import { getPaymentSummary, processPayment, getErrorMessage } from '../services/api.js'

function formatPrice(value) {
  return `$${Number(value).toLocaleString('es-CL')}`
}

// Agrega un espacio cada 4 digitos: "4111111111111111" -> "4111 1111 1111 1111"
function formatCardNumber(value) {
  const digits = value.replace(/\D/g, '').slice(0, 16)
  return digits.replace(/(.{4})/g, '$1 ').trim()
}

// Agrega "/" despues de los primeros 2 digitos: "1225" -> "12/25"
function formatExpirationDate(value) {
  const digits = value.replace(/\D/g, '').slice(0, 4)
  if (digits.length <= 2) return digits
  return `${digits.slice(0, 2)}/${digits.slice(2)}`
}

function PaymentPage() {
  const { bookingId } = useParams()
  const navigate = useNavigate()

  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  const [cardHolderName, setCardHolderName] = useState('')
  const [cardNumber, setCardNumber] = useState('')
  const [expirationDate, setExpirationDate] = useState('')
  const [cvv, setCvv] = useState('')

  useEffect(() => {
    getPaymentSummary(bookingId)
      .then(setSummary)
      .catch((err) => setLoadError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [bookingId])

  function validateForm() {
    if (cardHolderName.trim().length < 3) return 'Ingresa el nombre del titular tal como aparece en la tarjeta'
    if (cardNumber.replace(/\s/g, '').length !== 16) return 'El número de tarjeta debe tener 16 dígitos'
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(expirationDate)) return 'La fecha de expiración debe tener formato MM/YY'
    if (cvv.length !== 3) return 'El CVV debe tener 3 dígitos'
    return ''
  }

  function handleSubmit(event) {
    event.preventDefault()
    setError('')

    const validationError = validateForm()
    if (validationError) {
      setError(validationError)
      return
    }

    setSubmitting(true)
    processPayment({
      bookingId: Number(bookingId),
      amount: summary.totalAmount,
      cardNumber: cardNumber.replace(/\s/g, ''),
      expirationDate,
      cvv,
      cardHolderName: cardHolderName.trim(),
    })
      .then(() => navigate(`/payment-confirmation/${bookingId}`))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setSubmitting(false))
  }

  return (
    <div className="page">
      <Navbar />
      <div className="page-content payment-page">
        {loading && <p className="loading-text">Cargando resumen de pago...</p>}
        {loadError && <div className="alert alert-error">{loadError}</div>}

        {!loading && !loadError && summary && (
          <div className="payment-layout">
            <div className="payment-summary-card">
              <h2>Resumen de tu reserva</h2>
              <div className="summary-row">
                <span>Paquete</span>
                <span>{summary.packageName}</span>
              </div>
              <div className="summary-row">
                <span>Destino</span>
                <span>{summary.destination}</span>
              </div>
              <div className="summary-row">
                <span>Pasajeros</span>
                <span>{summary.passengerCount}</span>
              </div>
              <div className="summary-row">
                <span>Subtotal</span>
                <span>{formatPrice(summary.baseAmount)}</span>
              </div>
              {Number(summary.discountPercentage) > 0 && (
                <div className="summary-row summary-discount">
                  <span>Descuento ({summary.discountDetails})</span>
                  <span>-{summary.discountPercentage}%</span>
                </div>
              )}
              <div className="summary-row summary-total">
                <span>Total a pagar</span>
                <span>{formatPrice(summary.totalAmount)}</span>
              </div>
            </div>

            <form className="credit-card-form" onSubmit={handleSubmit}>
              <div className="credit-card-preview">
                <div className="credit-card-chip" />
                <div className="credit-card-number">{cardNumber || '•••• •••• •••• ••••'}</div>
                <div className="credit-card-bottom">
                  <span>{cardHolderName || 'NOMBRE DEL TITULAR'}</span>
                  <span>{expirationDate || 'MM/YY'}</span>
                </div>
              </div>

              <label className="form-field">
                <span>Nombre del titular</span>
                <input
                  value={cardHolderName}
                  onChange={(e) => setCardHolderName(e.target.value.toUpperCase())}
                  placeholder="NOMBRE APELLIDO"
                  required
                />
              </label>

              <label className="form-field">
                <span>Número de tarjeta</span>
                <input
                  value={cardNumber}
                  onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
                  placeholder="0000 0000 0000 0000"
                  inputMode="numeric"
                  required
                />
              </label>

              <div className="form-row">
                <label className="form-field">
                  <span>Fecha expiración</span>
                  <input
                    value={expirationDate}
                    onChange={(e) => setExpirationDate(formatExpirationDate(e.target.value))}
                    placeholder="MM/YY"
                    inputMode="numeric"
                    required
                  />
                </label>

                <label className="form-field">
                  <span>CVV</span>
                  <input
                    type="password"
                    value={cvv}
                    onChange={(e) => setCvv(e.target.value.replace(/\D/g, '').slice(0, 3))}
                    placeholder="•••"
                    inputMode="numeric"
                    required
                  />
                </label>
              </div>

              {error && <div className="alert alert-error">{error}</div>}

              <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
                {submitting ? 'Procesando pago...' : `Confirmar y Pagar ${formatPrice(summary.totalAmount)}`}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  )
}

export default PaymentPage
