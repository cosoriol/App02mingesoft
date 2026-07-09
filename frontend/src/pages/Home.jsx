import { useEffect, useState } from 'react'
import api from '../services/api.js'

// Lista de microservicios a monitorear, junto a su ruta de salud
const SERVICES = [
  { name: 'package-service', path: '/packages/health' },
  { name: 'search-service', path: '/search/health' },
  { name: 'booking-service', path: '/bookings/health' },
  { name: 'payment-service', path: '/payments/health' },
  { name: 'confirmation-service', path: '/confirmations/health' },
  { name: 'report-service', path: '/reports/health' },
]

// Pagina principal: muestra el estado de cada microservicio
function Home() {
  const [statuses, setStatuses] = useState({})

  useEffect(() => {
    SERVICES.forEach((service) => {
      api
        .get(service.path)
        .then((response) => {
          setStatuses((prev) => ({ ...prev, [service.name]: response.data.status }))
        })
        .catch(() => {
          setStatuses((prev) => ({ ...prev, [service.name]: 'down' }))
        })
    })
  }, [])

  return (
    <div style={{ fontFamily: 'sans-serif', padding: '2rem' }}>
      <h1>TravelAgency</h1>
      <h2>Estado de los microservicios</h2>
      <ul>
        {SERVICES.map((service) => (
          <li key={service.name}>
            {service.name}: {statuses[service.name] ?? 'cargando...'}
          </li>
        ))}
      </ul>
    </div>
  )
}

export default Home
