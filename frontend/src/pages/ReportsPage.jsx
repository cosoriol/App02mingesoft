import { useState } from 'react'
import Navbar from '../components/Navbar.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { getSalesReport, getPackageRanking, getErrorMessage } from '../services/api.js'

function todayIso() {
  return new Date().toISOString().slice(0, 10)
}

function firstDayOfMonthIso() {
  const now = new Date()
  return new Date(now.getFullYear(), now.getMonth(), 1).toISOString().slice(0, 10)
}

function formatPrice(value) {
  return `$${Number(value ?? 0).toLocaleString('es-CL')}`
}

// Dashboard de reportes de ventas y ranking de paquetes (Epica 7). Acceso exclusivo ADMIN
// (ver ProtectedRoute adminOnly en App.jsx); el backend tambien revalida el rol.
function ReportsPage() {
  const { role } = useAuth()
  const [startDate, setStartDate] = useState(firstDayOfMonthIso())
  const [endDate, setEndDate] = useState(todayIso())
  const [includeCancelled, setIncludeCancelled] = useState(false)

  const [sales, setSales] = useState(null)
  const [ranking, setRanking] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [hasGenerated, setHasGenerated] = useState(false)

  function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setLoading(true)

    const dateRange = { startDate, endDate, includeCancelled }
    Promise.all([getSalesReport(role, dateRange), getPackageRanking(role, { startDate, endDate })])
      .then(([salesData, rankingData]) => {
        setSales(salesData)
        setRanking(rankingData)
        setHasGenerated(true)
      })
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  return (
    <div className="page">
      <Navbar />
      <div className="page-content">
        <h1>Reportes</h1>

        <form className="filters-bar" onSubmit={handleSubmit}>
          <label className="form-field">
            <span>Desde</span>
            <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} required />
          </label>
          <label className="form-field">
            <span>Hasta</span>
            <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} required />
          </label>
          <label className="form-field">
            <span>
              <input
                type="checkbox"
                checked={includeCancelled}
                onChange={(e) => setIncludeCancelled(e.target.checked)}
              />{' '}
              Incluir canceladas
            </span>
          </label>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Generando...' : 'Generar'}
          </button>
        </form>

        {error && <div className="alert alert-error">{error}</div>}
        {!hasGenerated && !loading && !error && (
          <p className="empty-text">Elige un rango de fechas y presiona "Generar" para ver los reportes.</p>
        )}

        {sales && (
          <section className="report-section">
            <h2>Ventas</h2>
            <div className="stat-cards">
              <div className="stat-card">
                <p className="stat-card-label">Reservas</p>
                <p className="stat-card-value">{sales.summary.totalBookings}</p>
              </div>
              <div className="stat-card">
                <p className="stat-card-label">Pasajeros</p>
                <p className="stat-card-value">{sales.summary.totalPassengers}</p>
              </div>
              <div className="stat-card">
                <p className="stat-card-label">Monto total</p>
                <p className="stat-card-value">{formatPrice(sales.summary.totalSalesAmount)}</p>
              </div>
              <div className="stat-card">
                <p className="stat-card-label">Monto cobrado</p>
                <p className="stat-card-value">{formatPrice(sales.summary.totalCollectedAmount)}</p>
              </div>
            </div>

            {sales.items.length === 0 ? (
              <p className="empty-text">No hay ventas en este rango de fechas.</p>
            ) : (
              <div className="report-table-wrapper">
                <table className="report-table">
                  <thead>
                    <tr>
                      <th>Fecha</th>
                      <th>Cliente</th>
                      <th>Paquete</th>
                      <th>Destino</th>
                      <th>Pasajeros</th>
                      <th>Monto base</th>
                      <th>Descuento</th>
                      <th>Total</th>
                      <th>Pagado</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {sales.items.map((item, index) => (
                      <tr key={index}>
                        <td>{item.fecha}</td>
                        <td>
                          {item.clientName}
                          <br />
                          <span className="text-muted">{item.clientEmail}</span>
                        </td>
                        <td>{item.packageName}</td>
                        <td>{item.destination}</td>
                        <td>{item.passengerCount}</td>
                        <td>{formatPrice(item.baseAmount)}</td>
                        <td>
                          {item.discountPercentage}% ({formatPrice(item.discountAmount)})
                        </td>
                        <td>{formatPrice(item.totalAmount)}</td>
                        <td>{formatPrice(item.amountPaid)}</td>
                        <td>{item.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        )}

        {ranking && (
          <section className="report-section">
            <h2>Ranking de paquetes</h2>
            <div className="stat-cards">
              <div className="stat-card">
                <p className="stat-card-label">Paquetes con ventas</p>
                <p className="stat-card-value">{ranking.summary.totalPackagesWithSales}</p>
              </div>
              <div className="stat-card">
                <p className="stat-card-label">Reservas</p>
                <p className="stat-card-value">{ranking.summary.totalBookings}</p>
              </div>
              <div className="stat-card">
                <p className="stat-card-label">Pasajeros</p>
                <p className="stat-card-value">{ranking.summary.totalPassengers}</p>
              </div>
              <div className="stat-card">
                <p className="stat-card-label">Monto total</p>
                <p className="stat-card-value">{formatPrice(ranking.summary.totalAmount)}</p>
              </div>
            </div>

            {ranking.items.length === 0 ? (
              <p className="empty-text">No hay paquetes con ventas en este rango de fechas.</p>
            ) : (
              <div className="report-table-wrapper">
                <table className="report-table">
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Paquete</th>
                      <th>Destino</th>
                      <th>Reservas</th>
                      <th>Pasajeros</th>
                      <th>Monto total</th>
                      <th>Cobrado</th>
                      <th>Precio unitario</th>
                    </tr>
                  </thead>
                  <tbody>
                    {ranking.items.map((item) => (
                      <tr key={item.rank}>
                        <td>{item.rank}</td>
                        <td>{item.packageName}</td>
                        <td>{item.destination}</td>
                        <td>{item.bookingCount}</td>
                        <td>{item.totalPassengers}</td>
                        <td>{formatPrice(item.totalAmount)}</td>
                        <td>{formatPrice(item.totalCollected)}</td>
                        <td>{formatPrice(item.unitPrice)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        )}
      </div>
    </div>
  )
}

export default ReportsPage
