import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import { getAvailablePackages, searchPackages, getErrorMessage } from '../services/api.js'

const EMPTY_FILTERS = { destination: '', minPrice: '', maxPrice: '', startDate: '' }

function formatPrice(value) {
  return `$${Number(value).toLocaleString('es-CL')}`
}

// Catalogo de paquetes disponibles, con filtros de busqueda
function HomePage() {
  const [packages, setPackages] = useState([])
  const [filters, setFilters] = useState(EMPTY_FILTERS)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadAvailablePackages()
  }, [])

  function loadAvailablePackages() {
    setLoading(true)
    setError('')
    getAvailablePackages()
      .then(setPackages)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  function handleFilterChange(event) {
    const { name, value } = event.target
    setFilters((prev) => ({ ...prev, [name]: value }))
  }

  function handleSearch(event) {
    event.preventDefault()
    const hasActiveFilters = Object.values(filters).some((value) => value !== '')
    if (!hasActiveFilters) {
      loadAvailablePackages()
      return
    }

    setLoading(true)
    setError('')
    searchPackages(filters)
      .then(setPackages)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  function handleClearFilters() {
    setFilters(EMPTY_FILTERS)
    loadAvailablePackages()
  }

  return (
    <div className="page">
      <Navbar />
      <div className="page-content">
        <h1>Paquetes turísticos disponibles</h1>

        <form className="filters-bar" onSubmit={handleSearch}>
          <input
            name="destination"
            placeholder="Destino"
            value={filters.destination}
            onChange={handleFilterChange}
          />
          <input
            name="minPrice"
            type="number"
            min="0"
            placeholder="Precio mín."
            value={filters.minPrice}
            onChange={handleFilterChange}
          />
          <input
            name="maxPrice"
            type="number"
            min="0"
            placeholder="Precio máx."
            value={filters.maxPrice}
            onChange={handleFilterChange}
          />
          <input name="startDate" type="date" value={filters.startDate} onChange={handleFilterChange} />
          <button type="submit" className="btn btn-primary">
            Buscar
          </button>
          <button type="button" className="btn btn-outline" onClick={handleClearFilters}>
            Limpiar
          </button>
        </form>

        {error && <div className="alert alert-error">{error}</div>}
        {loading && <p className="loading-text">Cargando paquetes...</p>}
        {!loading && !error && packages.length === 0 && (
          <p className="empty-text">No se encontraron paquetes con estos criterios.</p>
        )}

        <div className="package-grid">
          {packages.map((pkg) => {
            const availableSlots = pkg.availableSlots ?? pkg.totalSlots - pkg.bookedSlots
            return (
              <div className="package-card" key={pkg.id}>
                <div className="package-card-header">
                  <h2>{pkg.name}</h2>
                  <span className="badge badge-destination">{pkg.destination}</span>
                </div>
                <p className="package-dates">
                  {pkg.startDate} → {pkg.endDate}
                </p>
                <p className="package-price">{formatPrice(pkg.price)}</p>
                <p className="package-slots">{availableSlots} cupos disponibles</p>
                <Link to={`/packages/${pkg.id}`} className="btn btn-primary btn-block">
                  Ver detalle
                </Link>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}

export default HomePage
